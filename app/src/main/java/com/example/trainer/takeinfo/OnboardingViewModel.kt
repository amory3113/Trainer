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

    // --- ДАННЫЕ ПОЛЬЗОВАТЕЛЯ ---
    private var userGender: Gender? = null
    private var userAge: Int = 0
    private var userHeight: Double = 0.0
    private var userWeight: Double = 0.0

    // Результаты (ActivityLevel мы уже сохраняем в activityLevelResult)
    private val _nutritionPlan = MutableStateFlow<NutritionPlan?>(null)
    val nutritionPlan = _nutritionPlan.asStateFlow()

    // Переменные из прошлого шага (Activity)
    private val _activityLevelResult = MutableStateFlow<ActivityLevel>(ActivityLevel.BEGINNER)

    // Храним результат тут (пока null)
    private val _healthResult = MutableStateFlow<HealthResult?>(null)
    val healthResult = _healthResult.asStateFlow()

    // 1. Сделай _userGoal потоком (StateFlow), чтобы LoadScreen узнал о цели
    private val _userGoal = MutableStateFlow<Goal?>(null)
    val userGoal = _userGoal.asStateFlow()

    // Новые переменные для тренировок
    // --- ИЗМЕНЕНИЕ: Делаем их StateFlow (потоками данных) ---
    private val _workoutLocation = MutableStateFlow<WorkoutLocation?>(null)
    val workoutLocation = _workoutLocation.asStateFlow()

    private val _workoutFrequency = MutableStateFlow<Int>(0)
    val workoutFrequency = _workoutFrequency.asStateFlow()



    // 1. Если на первом экране нажали "Нет ограничений"
    fun setHealthyStatus() {
        _healthResult.value = HealthResult(HealthStatus.GOOD, null)
        println("ЗДОРОВЬЕ: Установлено GOOD (Автоматически)")
    }

    // 2. Если прошли детальный опрос (считаем цифры)
    fun saveHealthData(
        chronic: Int,
        injuries: Int,
        heart: Int,
        restrictions: Int,
        fatigue: Int
    ) {
        val result = HealthAssessor.evaluate(chronic, injuries, heart, restrictions, fatigue)
        _healthResult.value = result
        println("ЗДОРОВЬЕ РАССЧИТАНО: ${result.status}")
    }

    // 1. Сохранение Пола
    fun setGender(gender: Gender) {
        userGender = gender
    }

    // 2. Сохранение Данных тела
    fun setBodyData(age: Int, height: Double, weight: Double) {
        userAge = age
        userHeight = height
        userWeight = weight
        println("DEBUG: Тело сохранено: $age лет, $weight кг")
    }

    // 3. Сохранение Цели
    fun setGoal(goal: Goal) {
        _userGoal.value = goal
    }

    // 4. Сохранение Активности (Это у тебя уже было, просто убедись, что сохраняешь результат)
    fun saveActivityData(job: Int, freq: Int, dur: Int, cond: Int, fatigue: Int) {
        val result = ActivityAnalyzer.analyze(job, freq, dur, cond, fatigue)
        _activityLevelResult.value = result // Сохраняем в переменную класса
    }

    // 5. ФИНАЛЬНЫЙ РАСЧЕТ (Вызывается на экране загрузки)
    fun calculateNutrition() {
        val goalValue = _userGoal.value
        if (userGender != null && goalValue != null && userWeight > 0) {
            val plan = NutritionCalculator.calculate(
                gender = userGender!!,
                weight = userWeight,
                height = userHeight,
                age = userAge,
                activityLevel = _activityLevelResult.value, // Берем рассчитанную активность
                goal = goalValue!!
            )
            _nutritionPlan.value = plan
            println("ПЛАН ПИТАНИЯ: ${plan.calories} ккал. Б:${plan.protein} Ж:${plan.fat} У:${plan.carbs}")
        }
    }

    // Функция сохранения
    fun saveWorkoutPreferences(location: WorkoutLocation, frequency: Int) {
        _workoutLocation.value = location // Используем .value
        _workoutFrequency.value = frequency // Используем .value
        println("DEBUG: Тренировки -> Место: $location, Раз в неделю: $frequency")
    }

    fun saveFinalDataToDatabase() {
        // Запускаем в фоновом потоке (viewModelScope)
        viewModelScope.launch {
            // Проверяем, что всё необходимое есть (на всякий случай)
            if (userGender != null && _userGoal.value != null && _nutritionPlan.value != null) {

                repository.saveUserProfile(
                    gender = userGender!!,
                    age = userAge,
                    weight = userWeight,
                    height = userHeight,
                    goal = _userGoal.value!!,
                    activityLevel = _activityLevelResult.value,
                    healthResult = _healthResult.value, // Может быть null, и это ок
                    workoutLocation = _workoutLocation.value ?: WorkoutLocation.HOME, // Дефолт если что
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