package com.example.trainer.GeneralScreen.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val userProfile: UserEntity? = null,
    val caloriesEaten: Int = 0,
    val proteinEaten: Int = 0,
    val fatEaten: Int = 0,
    val carbsEaten: Int = 0,
    val todayWorkoutName: String? = null,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.userFlow.collect { user ->
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        userProfile = user,
                        isLoading = false
                    )
                }
            }
        }

        loadNutritionData()
        viewModelScope.launch {
            workoutRepository.schedule.collect { scheduleList ->
                val todayIndex = getTodayIndex()
                val todayItem = scheduleList.find { it.dayOfWeek == todayIndex }
                _uiState.value = _uiState.value.copy(
                    todayWorkoutName = todayItem?.workoutName
                )
            }
        }
    }
    fun loadNutritionData() {
        viewModelScope.launch {
            val todayNutrition = userRepository.getTodayNutrition()
            _uiState.value = _uiState.value.copy(
                caloriesEaten = todayNutrition?.calories ?: 0,
                proteinEaten = todayNutrition?.protein ?: 0,
                fatEaten = todayNutrition?.fat ?: 0,
                carbsEaten = todayNutrition?.carbs ?: 0,
                isLoading = false
            )
        }
    }
    private fun getTodayIndex(): Int {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }
    fun getTodayName(): String {
        val days = listOf("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela")
        return days[getTodayIndex()]
    }
    fun addFood(kcal: Int, protein: Int, fat: Int, carbs: Int) {
        viewModelScope.launch {
            userRepository.addFood(kcal, protein, fat, carbs)
            loadNutritionData()
        }
    }
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

