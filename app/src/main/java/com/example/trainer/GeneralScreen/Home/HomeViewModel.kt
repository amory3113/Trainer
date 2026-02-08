package com.example.trainer.GeneralScreen.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.UserEntity
import com.example.trainer.data.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

// Модель для отображения упражнения на Главном экране
data class DailyExercise(
    val exercise: ExerciseEntity,
    val sets: Int,
    val reps: Int
)

data class HomeUiState(
    val userProfile: UserEntity? = null,
    val caloriesEaten: Int = 0,
    val proteinEaten: Int = 0,
    val fatEaten: Int = 0,
    val carbsEaten: Int = 0,
    val todayWorkoutName: String? = null,
    val todayExercises: List<DailyExercise> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class) // Нужно для flatMapLatest
class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // 1. Подписка на профиль
        viewModelScope.launch {
            userRepository.userFlow.collect { user ->
                if (user != null) {
                    _uiState.value = _uiState.value.copy(
                        userProfile = user,
                        isLoading = false
                    )
                    loadNutritionData()
                }
            }
        }

        // 2. УМНАЯ ПОДПИСКА НА ТРЕНИРОВКУ
        observeTodayWorkout()
    }

    private fun observeTodayWorkout() {
        viewModelScope.launch {
            // Следим за расписанием
            workoutRepository.schedule
                // 1. Превращаем список расписания в конкретную запись "на сегодня"
                .map { scheduleList ->
                    val todayIndex = getTodayIndex()
                    scheduleList.find { it.dayOfWeek == todayIndex }
                }
                // 2. САМАЯ ВАЖНАЯ ЧАСТЬ: flatMapLatest
                // Если запись на сегодня есть -> переключаемся на прослушивание упражнений этой тренировки
                // Если записи нет -> возвращаем пустой поток
                .flatMapLatest { todayItem ->
                    if (todayItem != null && todayItem.workoutId != null) {
                        // Подписываемся на изменения ВНУТРИ тренировки (подходы/повторы)
                        workoutRepository.getWorkoutExercisesStream(todayItem.workoutId)
                            .map { rawExercises ->
                                // Возвращаем пару: (Название тренировки, Список упражнений)
                                todayItem.workoutName to rawExercises
                            }
                    } else {
                        // Если тренировки нет, возвращаем null и пустой список
                        flowOf(null to emptyList())
                    }
                }
                // 3. Получаем готовые данные и обновляем UI
                .collect { (workoutName, rawExercises) ->

                    val fullExercises = mutableListOf<DailyExercise>()

                    // Превращаем "сырые" данные в красивые объекты для UI
                    for (raw in rawExercises) {
                        // Здесь getExerciseById все еще одноразовый запрос, но это ок,
                        // так как названия упражнений меняются редко.
                        // Главное, что raw.sets и raw.reps приходят свежими.
                        val exerciseInfo = workoutRepository.getExerciseById(raw.exerciseId)
                        if (exerciseInfo != null) {
                            fullExercises.add(DailyExercise(
                                exercise = exerciseInfo,
                                sets = raw.sets,
                                reps = raw.reps
                            ))
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        todayWorkoutName = workoutName,
                        todayExercises = fullExercises
                    )
                }
        }
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ---
    fun getTodayIndex(): Int {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        // В Calendar: Sunday=1, Monday=2 ... Saturday=7
        // В нашей базе: Monday=0 ... Sunday=6
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

    // --- ПИТАНИЕ ---
    fun loadNutritionData() {
        viewModelScope.launch {
            val start = getStartOfDay()
            val end = getEndOfDay()
            val todayNutrition = userRepository.getNutritionForDate(start, end)

            if (todayNutrition != null) {
                _uiState.value = _uiState.value.copy(
                    caloriesEaten = todayNutrition.calories,
                    proteinEaten = todayNutrition.protein,
                    fatEaten = todayNutrition.fat,
                    carbsEaten = todayNutrition.carbs
                )
            } else {
                _uiState.value = _uiState.value.copy(caloriesEaten = 0, proteinEaten = 0, fatEaten = 0, carbsEaten = 0)
            }
        }
    }

    fun addFood(kcal: Int, protein: Int, fat: Int, carbs: Int) {
        viewModelScope.launch {
            userRepository.addFood(kcal, protein, fat, carbs)
            loadNutritionData()
        }
    }

    // --- ПРОГРЕСС ---
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

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
}