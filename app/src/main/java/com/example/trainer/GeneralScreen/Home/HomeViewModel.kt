package com.example.trainer.GeneralScreen.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val userProfile: UserEntity? = null,
    val caloriesEaten: Int = 0,
    val proteinEaten: Int = 0,
    val fatEaten: Int = 0,
    val carbsEaten: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    // Эта функция теперь загружает И профиль, И съеденное за сегодня
    fun loadData() {
        viewModelScope.launch {
            val profile = userRepository.getUserProfile()
            val todayNutrition = userRepository.getTodayNutrition() // <-- БЕРЕМ ИЗ БАЗЫ

            _uiState.value = _uiState.value.copy(
                userProfile = profile,
                // Если записи нет (null), значит 0, иначе берем из базы
                caloriesEaten = todayNutrition?.calories ?: 0,
                proteinEaten = todayNutrition?.protein ?: 0,
                fatEaten = todayNutrition?.fat ?: 0,
                carbsEaten = todayNutrition?.carbs ?: 0,
                isLoading = false
            )
        }
    }

    // Функция добавления теперь пишет в базу
    fun addMockFood() {
        viewModelScope.launch {
            // Добавляем тестовый обед: 500 ккал, 30 белка, 15 жира, 50 углей
            userRepository.addFood(500, 30, 15, 50)

            // ВАЖНО: После записи в базу, нужно обновить экран (перечитать данные)
            loadData()
        }
    }

    // Вспомогательные функции для расчета остатков и прогресса
    fun getCaloriesRemaining(): Int {
        val target = _uiState.value.userProfile?.targetCalories ?: 0
        return (target - _uiState.value.caloriesEaten).coerceAtLeast(0)
    }

    fun getCaloriesProgress(): Float {
        val target = _uiState.value.userProfile?.targetCalories ?: 1
        return (_uiState.value.caloriesEaten.toFloat() / target).coerceIn(0f, 1f)
    }

    fun getProteinProgress(): Float {
        val target = _uiState.value.userProfile?.proteinGrams ?: 1
        return (_uiState.value.proteinEaten.toFloat() / target).coerceIn(0f, 1f)
    }

    fun getFatProgress(): Float {
        val target = _uiState.value.userProfile?.fatGrams ?: 1
        return (_uiState.value.fatEaten.toFloat() / target).coerceIn(0f, 1f)
    }

    fun getCarbsProgress(): Float {
        val target = _uiState.value.userProfile?.carbGrams ?: 1
        return (_uiState.value.carbsEaten.toFloat() / target).coerceIn(0f, 1f)
    }
}

