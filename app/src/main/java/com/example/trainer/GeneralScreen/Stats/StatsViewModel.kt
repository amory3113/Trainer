package com.example.trainer.GeneralScreen.Stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.NutritionEntity
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class StatsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserEntity?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _weightHistory = MutableStateFlow<List<Float>>(emptyList())
    val weightHistory = _weightHistory.asStateFlow()

    // Список питания
    private val _nutritionHistory = MutableStateFlow<List<NutritionEntity>>(emptyList())
    val nutritionHistory = _nutritionHistory.asStateFlow()

    init {
        // 1. Слушаем профиль
        viewModelScope.launch {
            repository.userFlow.collect { user ->
                _userProfile.value = user
            }
        }

        // 2. Слушаем историю веса
        viewModelScope.launch {
            repository.getWeightHistoryFlow().collect { listEntities ->
                _weightHistory.value = listEntities.map { it.weight }
            }
        }

        // 3. СЛУШАЕМ ИСТОРИЮ ПИТАНИЯ (НОВОЕ)
        // Теперь мы используем .collect, чтобы данные обновлялись сами
        viewModelScope.launch {
            repository.getNutritionHistoryStream().collect { list ->
                _nutritionHistory.value = list
            }
        }
    }

    // Функция loadNutritionData() больше не нужна, так как работает init выше.

    // ИСПРАВЛЕННАЯ ФУНКЦИЯ: Добавляет вес И пересчитывает калории
    fun addNewWeight(newWeight: Float) {
        viewModelScope.launch {
            // 1. Добавляем запись в историю (для графика)
            repository.addWeightEntry(newWeight)

            // 2. Пересчитываем калории для профиля
            val currentUser = _userProfile.value
            if (currentUser != null) {
                val updatedUser = recalculateNutrition(currentUser, newWeight)
                // 3. Сохраняем обновленного пользователя в базу
                repository.updateUser(updatedUser)
            }
        }
    }

    // Логика пересчета (Такая же, как при регистрации)
    private fun recalculateNutrition(user: UserEntity, currentWeight: Float): UserEntity {
        // Формула Миффлина-Сан-Жеора
        val bmr = if (user.gender == "MALE") {
            (10 * currentWeight) + (6.25 * user.height) - (5 * user.age) + 5
        } else {
            (10 * currentWeight) + (6.25 * user.height) - (5 * user.age) - 161
        }

        // Коэффициент активности (Парсим из строки или числового значения ActivityLevel)
        // Предполагаем, что в базе ActivityLevel хранится как Enum name, но на всякий случай проверяем
        val activityMultiplier = when (user.activityLevel) {
            "SEDENTARY" -> 1.2
            "LIGHTLY_ACTIVE" -> 1.375
            "MODERATELY_ACTIVE" -> 1.55
            "VERY_ACTIVE" -> 1.725
            "SUPER_ACTIVE" -> 1.9
            else -> 1.2 // Дефолт
        }

        val tdee = bmr * activityMultiplier

        // Корректировка под цель
        val goalAdjustment = when (user.goal) {
            "WEIGHT_LOSS" -> -400 // Дефицит
            "MUSCLE_GAIN" -> 300  // Профицит
            else -> 0
        }

        val targetCal = (tdee + goalAdjustment).toInt().coerceAtLeast(1200) // Защита от краша (минимум 1200)

        // БЖУ
        val p = (targetCal * 0.3 / 4).toInt()
        val f = (targetCal * 0.3 / 9).toInt()
        val c = (targetCal * 0.4 / 4).toInt()

        return user.copy(
            weight = currentWeight.toDouble(),
            targetCalories = targetCal,
            proteinGrams = p,
            fatGrams = f,
            carbGrams = c
        )
    }

    fun calculateBMI(): Double {
        val user = _userProfile.value ?: return 0.0
        val heightM = user.height / 100.0
        return if (heightM > 0) user.weight / heightM.pow(2) else 0.0
    }

    fun getBmiStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Deficyt wagi"
            bmi < 25.0 -> "Norma"
            bmi < 30.0 -> "Nadwaga"
            else -> "Otyłość"
        }
    }
}

class StatsViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}