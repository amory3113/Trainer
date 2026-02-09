package com.example.trainer.takeinfo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainer.Logic.ActivityAnalyzer
import com.example.trainer.Logic.HealthAssessor
import com.example.trainer.Logic.Models.ActivityLevel
import com.example.trainer.Logic.Models.Gender
import com.example.trainer.Logic.Models.Goal
import com.example.trainer.Logic.Models.HealthResult
import com.example.trainer.Logic.Models.HealthStatus
import com.example.trainer.Logic.Models.NutritionPlan
import com.example.trainer.Logic.Models.WorkoutLocation
import com.example.trainer.Logic.NutritionCalculator
import com.example.trainer.Logic.WorkoutGenerator
import com.example.trainer.data.Exercise.ExerciseDao
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseDao: ExerciseDao
) : ViewModel() {

    private var userGender: Gender? = null
    private var userAge: Int = 0
    private var userHeight: Double = 0.0
    private var userWeight: Double = 0.0

    private val _nutritionPlan = MutableStateFlow<NutritionPlan?>(null)
    val nutritionPlan = _nutritionPlan.asStateFlow()

    private val _activityLevelResult = MutableStateFlow(ActivityLevel.BEGINNER)
    private val _healthResult = MutableStateFlow<HealthResult?>(null)

    private val _userGoal = MutableStateFlow<Goal?>(null)
    val userGoal = _userGoal.asStateFlow()
    private val _workoutLocation = MutableStateFlow<WorkoutLocation?>(null)
    val workoutLocation = _workoutLocation.asStateFlow()
    private val _workoutFrequency = MutableStateFlow(3)
    val workoutFrequency = _workoutFrequency.asStateFlow()
    fun setGender(gender: Gender) {
        userGender = gender
    }

    fun setBodyData(age: Int, height: Double, weight: Double) {
        userAge = age
        userHeight = height
        userWeight = weight
    }

    fun setGoal(goal: Goal) {
        _userGoal.value = goal
    }

    fun calculateActivityLevel(job: Int, sport: Int, duration: Int, condition: Int, fatigue: Int) {
        val result = ActivityAnalyzer.analyze(job, sport, duration, condition, fatigue)
        _activityLevelResult.value = result
    }

    fun setHealthyStatus() {
        _healthResult.value = HealthResult(HealthStatus.GOOD, null)
    }

    fun calculateHealthRisks(chronic: Int, injuries: Int, heart: Int, restrictions: Int, fatigue: Int) {
        val result = HealthAssessor.evaluate(chronic, injuries, heart, restrictions, fatigue)
        _healthResult.value = result
    }

    fun calculateNutrition() {
        if (userGender != null && _userGoal.value != null) {
            val plan = NutritionCalculator.calculate(
                gender = userGender!!,
                weight = userWeight,
                height = userHeight,
                age = userAge,
                activityLevel = _activityLevelResult.value,
                goal = _userGoal.value!!
            )
            _nutritionPlan.value = plan
        }
    }

    fun saveWorkoutPreferences(location: WorkoutLocation, frequency: Int) {
        _workoutLocation.value = location
        _workoutFrequency.value = frequency
    }

    fun saveFinalDataToDatabase(context: Context) {
        viewModelScope.launch {
            if (userGender != null && _userGoal.value != null && _nutritionPlan.value != null) {

                repository.saveUserProfile(
                    gender = userGender!!,
                    age = userAge,
                    weight = userWeight,
                    height = userHeight,
                    goal = _userGoal.value!!,
                    activityLevel = _activityLevelResult.value,
                    healthResult = _healthResult.value,
                    workoutLocation = _workoutLocation.value ?: WorkoutLocation.HOME,
                    workoutFrequency = _workoutFrequency.value,
                    nutritionPlan = _nutritionPlan.value!!
                )

                val tempUserForGenerator = UserEntity(
                    gender = userGender!!.name,
                    age = userAge,
                    weight = userWeight,
                    height = userHeight,
                    goal = _userGoal.value!!.name,
                    activityLevel = _activityLevelResult.value.name,
                    healthStatus = "UNKNOWN",
                    healthWarning = null,
                    workoutLocation = _workoutLocation.value?.name ?: "HOME",
                    workoutFrequency = _workoutFrequency.value,
                    targetCalories = 0, proteinGrams = 0, fatGrams = 0, carbGrams = 0
                )

                val allExercises = exerciseDao.getAllExercises().first()
                val generatedPlan = WorkoutGenerator.generate(tempUserForGenerator, allExercises)

                val tempToRealIdMap = mutableMapOf<Long, Int>()

                generatedPlan.workouts.forEach { generatedWorkout ->
                    val realId = workoutRepository.createTemplate(
                        generatedWorkout.template.name,
                        generatedWorkout.template.description
                    ).toInt()

                    tempToRealIdMap[generatedWorkout.tempId] = realId

                    val entities = generatedWorkout.exercises.map {
                        it.copy(workoutId = realId, id = 0)
                    }
                    workoutRepository.updateWorkout(realId, generatedWorkout.template.name, entities)
                }


                generatedPlan.schedule.forEach { (day, tempId) ->
                    val realId = tempToRealIdMap[tempId]
                    if (realId != null) {
                        val workoutName = generatedPlan.workouts.find { it.tempId == tempId }?.template?.name ?: ""
                        workoutRepository.setWorkoutToDay(day, realId, workoutName)
                    }
                }
                println("DATABASE: План успешно сгенерирован! Сохранено тренировок: ${generatedPlan.workouts.size}")
            }
            com.example.trainer.notification.NotificationScheduler.scheduleAllNotifications(context)
        }
    }
}