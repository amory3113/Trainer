package com.example.trainer.takeinfo

import androidx.lifecycle.ViewModel
import com.example.trainer.Logic.ActivityAnalyzer
import com.example.trainer.Logic.HealthAssessor
import androidx.lifecycle.viewModelScope
import com.example.trainer.Logic.Models.ActivityLevel
import com.example.trainer.Logic.Models.Gender // Импорт
import com.example.trainer.Logic.Models.Goal   // Импорт
import com.example.trainer.Logic.Models.HealthResult
import com.example.trainer.data.UserRepository
import com.example.trainer.Logic.Models.HealthStatus
import com.example.trainer.Logic.Models.NutritionPlan // Импорт
import com.example.trainer.Logic.Models.WorkoutLocation
import com.example.trainer.Logic.NutritionCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repository: UserRepository) : ViewModel() {
    private var userGender: Gender? = null
    private var userAge: Int = 0
    private var userHeight: Double = 0.0
    private var userWeight: Double = 0.0
    private val _nutritionPlan = MutableStateFlow<NutritionPlan?>(null)
    val nutritionPlan = _nutritionPlan.asStateFlow()

    private val _activityLevelResult = MutableStateFlow<ActivityLevel>(ActivityLevel.BEGINNER)
    private val _healthResult = MutableStateFlow<HealthResult?>(null)
    val healthResult = _healthResult.asStateFlow()
    private val _userGoal = MutableStateFlow<Goal?>(null)
    val userGoal = _userGoal.asStateFlow()
    private val _workoutLocation = MutableStateFlow<WorkoutLocation?>(null)
    val workoutLocation = _workoutLocation.asStateFlow()

    private val _workoutFrequency = MutableStateFlow<Int>(0)
    val workoutFrequency = _workoutFrequency.asStateFlow()

    fun setHealthyStatus() {
        _healthResult.value = HealthResult(HealthStatus.GOOD, null)
        println("ZDROWIE: Ustaw na DOBRE (automatyczne)")
    }
    fun saveHealthData(
        chronic: Int,
        injuries: Int,
        heart: Int,
        restrictions: Int,
        fatigue: Int
    ) {
        val result = HealthAssessor.evaluate(chronic, injuries, heart, restrictions, fatigue)
        _healthResult.value = result
        println("ZDROWIE OBLICZONE: ${result.status}")
    }

    fun setGender(gender: Gender) {
        userGender = gender
    }

    fun setBodyData(age: Int, height: Double, weight: Double) {
        userAge = age
        userHeight = height
        userWeight = weight
        println("DEBUG: Treść zapisana: $age lat, $weight kg")
    }

    fun setGoal(goal: Goal) {
        _userGoal.value = goal
    }
    fun saveActivityData(job: Int, freq: Int, dur: Int, cond: Int, fatigue: Int) {
        val result = ActivityAnalyzer.analyze(job, freq, dur, cond, fatigue)
        _activityLevelResult.value = result
    }

    fun calculateNutrition() {
        val goalValue = _userGoal.value
        if (userGender != null && goalValue != null && userWeight > 0) {
            val plan = NutritionCalculator.calculate(
                gender = userGender!!,
                weight = userWeight,
                height = userHeight,
                age = userAge,
                activityLevel = _activityLevelResult.value,
                goal = goalValue!!
            )
            _nutritionPlan.value = plan
            println("PLAN POSIŁKÓW: ${plan.calories} kcal. B:${plan.protein} T:${plan.fat} W:${plan.carbs}")
        }
    }

    fun saveWorkoutPreferences(location: WorkoutLocation, frequency: Int) {
        _workoutLocation.value = location
        _workoutFrequency.value = frequency
        println("DEBUG: Szkolenie -> Lokalizacja: $location, Raz w tygodniu: $frequency")
    }

    fun saveFinalDataToDatabase() {
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
                println("DATABASE: Все данные успешно сохранены в Room!")
            } else {
                println("DATABASE ERROR: Чего-то не хватает для сохранения.")
            }
        }
    }
}