package com.example.trainer.GeneralScreen.Workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.data.Exercise.ScheduleEntity
import com.example.trainer.data.Exercise.WorkoutExerciseEntity
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.Exercise.WorkoutTemplateEntity
import com.example.trainer.data.Exercise.WorkoutWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val exerciseDao: com.example.trainer.data.Exercise.ExerciseDao
) : ViewModel() {


    // ID тренировки, которую мы сейчас редактируем (null = создание новой)
    private var currentEditingId: Int? = null

    // Название тренировки (вынесли в StateFlow, чтобы экран видел изменения при загрузке)
    private val _workoutName = MutableStateFlow("")
    val workoutName = _workoutName.asStateFlow()

    // --- ДАННЫЕ ---
    private val _templates = MutableStateFlow<List<WorkoutWithExercises>>(emptyList())
    val templates = _templates.asStateFlow()

    private val _schedule = MutableStateFlow<List<ScheduleEntity>>(emptyList())
    val schedule = _schedule.asStateFlow()

    private val _allExercisesRaw = MutableStateFlow<List<ExerciseEntity>>(emptyList())

    // --- ФИЛЬТРАЦИЯ ПО КАТЕГОРИЯМ ---
    // Выбранная категория (null = Все)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    // Список категорий для кнопок (Грудь, Спина...)
    val categories = listOf("Все", "CHEST", "BACK", "LEGS", "ARMS", "ABS", "SHOULDERS")
    // В идеале брать названия из ресурсов или перевода, но пока так для простоты

    // Умный список: зависит от выбранной категории
    val filteredExercises = combine(_allExercisesRaw, _selectedCategory) { list, category ->
        if (category == null || category == "Все") {
            list
        } else {
            list.filter { it.muscleGroup.equals(category, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Список упражнений ВНУТРИ новой тренировки
    private val _selectedExercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val selectedExercises = _selectedExercises.asStateFlow()

    init {
        viewModelScope.launch { repository.allTemplates.collect { _templates.value = it } }
        viewModelScope.launch { repository.schedule.collect { _schedule.value = it } }
        viewModelScope.launch { exerciseDao.getAllExercises().collect { _allExercisesRaw.value = it } }
    }

    // Смена категории фильтра
    fun selectCategory(category: String) {
        _selectedCategory.value = if (category == "Все") null else category
    }

    // Галочки (добавить/убрать упражнение)
    fun toggleExerciseSelection(exercise: ExerciseEntity) {
        val currentList = _selectedExercises.value.toMutableList()
        if (currentList.contains(exercise)) {
            currentList.remove(exercise)
        } else {
            currentList.add(exercise)
        }
        _selectedExercises.value = currentList
    }

    // --- СОХРАНЕНИЕ (ОБНОВЛЕННОЕ) ---
    fun saveWorkout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val nameToSave = if (_workoutName.value.isBlank()) "Новая тренировка" else _workoutName.value

            // Если ID есть -> ОБНОВЛЯЕМ, иначе -> СОЗДАЕМ
            val targetId = currentEditingId

            if (targetId != null) {
                // РЕДАКТИРОВАНИЕ
                val exerciseLinks = _selectedExercises.value.mapIndexed { index, exercise ->
                    WorkoutExerciseEntity(
                        workoutId = targetId,
                        exerciseId = exercise.id,
                        sets = 3, reps = 10, order = index
                    )
                }
                repository.updateWorkout(targetId, nameToSave, exerciseLinks)
            } else {
                // СОЗДАНИЕ НОВОЙ
                val newId = repository.createTemplate(nameToSave, "Пользовательская")
                _selectedExercises.value.forEachIndexed { index, exercise ->
                    val link = WorkoutExerciseEntity(
                        workoutId = newId.toInt(),
                        exerciseId = exercise.id,
                        sets = 3, reps = 10, order = index
                    )
                    repository.insertWorkoutExercise(link)
                }
            }

            clearSelection()
            onSuccess()
        }
    }

    fun clearSelection() {
        _selectedExercises.value = emptyList()
        _workoutName.value = ""
        currentEditingId = null
        _selectedCategory.value = null
    }

    // --- ЛОГИКА РАСПИСАНИЯ (Привязка к дню) ---
    fun assignWorkoutToDay(dayIndex: Int, workoutWrapper: WorkoutWithExercises) {
        viewModelScope.launch {
            repository.setWorkoutToDay(dayIndex, workoutWrapper.template.id, workoutWrapper.template.name)
        }
    }

    // НОВАЯ ФУНКЦИЯ: Сделать день выходным
    fun clearDay(dayIndex: Int) {
        viewModelScope.launch {
            repository.clearDay(dayIndex)
        }
    }

    fun deleteWorkout(workoutId: Int) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId)
        }
    }

    fun onNameChange(newName: String) {
        _workoutName.value = newName
    }

    // --- ЗАГРУЗКА ДЛЯ РЕДАКТИРОВАНИЯ ---
    fun loadWorkoutForEdit(workoutId: Int) {
        if (workoutId == -1) {
            // Режим создания: сбрасываем всё
            clearSelection()
            return
        }

        viewModelScope.launch {
            val template = repository.getTemplateById(workoutId)
            val exercises = repository.getExercisesForTemplate(workoutId)

            if (template != null) {
                currentEditingId = workoutId
                _workoutName.value = template.name
                _selectedExercises.value = exercises
            }
        }
    }


}

class WorkoutViewModelFactory(
    private val repository: WorkoutRepository,
    private val exerciseDao: com.example.trainer.data.Exercise.ExerciseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository, exerciseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}