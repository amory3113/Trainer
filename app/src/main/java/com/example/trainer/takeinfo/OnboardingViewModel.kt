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

// Добавили workoutRepository и exerciseDao в конструктор
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

    private val _activityLevelResult = MutableStateFlow<ActivityLevel>(ActivityLevel.BEGINNER)
    private val _healthResult = MutableStateFlow<HealthResult?>(null)

    private val _userGoal = MutableStateFlow<Goal?>(null)
    val userGoal = _userGoal.asStateFlow()
    private val _workoutLocation = MutableStateFlow<WorkoutLocation?>(null)
    val workoutLocation = _workoutLocation.asStateFlow()
    private val _workoutFrequency = MutableStateFlow<Int>(3)
    val workoutFrequency = _workoutFrequency.asStateFlow()

    // --- Сбор данных ---

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

    // --- ФИНАЛЬНОЕ СОХРАНЕНИЕ ---

    fun saveFinalDataToDatabase(context: Context) {
        viewModelScope.launch {
            if (userGender != null && _userGoal.value != null && _nutritionPlan.value != null) {

                // 1. Сохраняем профиль пользователя и вес
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

                // 2. ГЕНЕРАЦИЯ ТРЕНИРОВОК
                // Создаем временный объект юзера, чтобы передать в генератор (ID не важен)
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
                    targetCalories = 0, proteinGrams = 0, fatGrams = 0, carbGrams = 0 // заглушки
                )

                // Получаем все упражнения из базы (они уже загрузились при старте App)
                val allExercises = exerciseDao.getAllExercises().first()

                // Запускаем генератор!
                val generatedPlan = WorkoutGenerator.generate(tempUserForGenerator, allExercises)

                // 3. Сохраняем сгенерированный план в БД
                val tempToRealIdMap = mutableMapOf<Long, Int>()

                // Определяем ID шаблонов из генератора
                val isFullBody = _workoutFrequency.value <= 3
                val firstTempId = if (isFullBody) 1L else 10L
                val secondTempId = if (isFullBody) 2L else 20L

                // Сохраняем первую тренировку (A или Upper)
                if (generatedPlan.templates.isNotEmpty()) {
                    val t1 = generatedPlan.templates[0]
                    val realId1 = workoutRepository.createTemplate(t1.name, t1.description).toInt()
                    tempToRealIdMap[firstTempId] = realId1

                    // Сохраняем упражнения для первой тренировки
                    val exercises1 = generatedPlan.exercisesMap[firstTempId] ?: emptyList()
                    val entities1 = exercises1.map { it.copy(workoutId = realId1, id = 0) }
                    workoutRepository.updateWorkout(realId1, t1.name, entities1)
                }

                // Сохраняем вторую тренировку (B или Lower), если она есть
                if (generatedPlan.templates.size > 1) {
                    val t2 = generatedPlan.templates[1]
                    val realId2 = workoutRepository.createTemplate(t2.name, t2.description).toInt()
                    tempToRealIdMap[secondTempId] = realId2

                    // Сохраняем упражнения для второй тренировки
                    val exercises2 = generatedPlan.exercisesMap[secondTempId] ?: emptyList()
                    val entities2 = exercises2.map { it.copy(workoutId = realId2, id = 0) }
                    workoutRepository.updateWorkout(realId2, t2.name, entities2)
                }

                // 4. Заполняем расписание
                generatedPlan.schedule.forEach { (day, tempId) ->
                    val realId = tempToRealIdMap[tempId]
                    if (realId != null) {
                        // Находим имя для расписания
                        val name = if (tempId == firstTempId) generatedPlan.templates[0].name
                        else generatedPlan.templates.getOrNull(1)?.name ?: ""

                        workoutRepository.setWorkoutToDay(day, realId, name)
                    }
                }

                println("DATABASE: План успешно сгенерирован и сохранен!")
            }

            // 5. Запускаем уведомления
            com.example.trainer.notification.NotificationScheduler.scheduleAllNotifications(context)
        }
    }
}