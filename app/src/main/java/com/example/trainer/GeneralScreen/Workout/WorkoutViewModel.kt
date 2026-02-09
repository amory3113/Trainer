package com.example.trainer.GeneralScreen.Workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.data.Exercise.ScheduleEntity
import com.example.trainer.data.Exercise.WorkoutExerciseEntity
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.Exercise.WorkoutWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WorkoutExerciseUiState(
    val exercise: ExerciseEntity,
    val sets: Int = 3,
    val reps: Int = 12
)

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val exerciseDao: com.example.trainer.data.Exercise.ExerciseDao
) : ViewModel() {

    private var currentEditingId: Int? = null

    private val _workoutName = MutableStateFlow("")
    val workoutName = _workoutName.asStateFlow()

    private val _templates = MutableStateFlow<List<WorkoutWithExercises>>(emptyList())
    val templates = _templates.asStateFlow()

    private val _schedule = MutableStateFlow<List<ScheduleEntity>>(emptyList())
    val schedule = _schedule.asStateFlow()

    private val _allExercisesRaw = MutableStateFlow<List<ExerciseEntity>>(emptyList())

    private val _selectedExercises = MutableStateFlow<List<WorkoutExerciseUiState>>(emptyList())
    val selectedExercises = _selectedExercises.asStateFlow()

    val availableExercises = combine(_allExercisesRaw, _selectedExercises) { all, selected ->
        val selectedIds = selected.map { it.exercise.id }
        all.filter { it.id !in selectedIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.allTemplates.collect { _templates.value = it }
        }
        viewModelScope.launch {
            repository.schedule.collect { _schedule.value = it }
        }
        viewModelScope.launch {
            exerciseDao.getAllExercises().collect { _allExercisesRaw.value = it }
        }
    }

    fun addExercise(exercise: ExerciseEntity) {
        val newItem = WorkoutExerciseUiState(exercise, sets = 3, reps = 12)
        _selectedExercises.value = _selectedExercises.value + newItem
    }

    fun removeExercise(exerciseId: Int) {
        _selectedExercises.value = _selectedExercises.value.filter { it.exercise.id != exerciseId }
    }

    fun updateExerciseDetails(exerciseId: Int, newSets: Int, newReps: Int) {
        _selectedExercises.value = _selectedExercises.value.map { item ->
            if (item.exercise.id == exerciseId) {
                item.copy(sets = newSets, reps = newReps)
            } else {
                item
            }
        }
    }

    fun saveWorkout() {
        val name = _workoutName.value
        if (name.isBlank() || _selectedExercises.value.isEmpty()) return

        viewModelScope.launch {
            val workoutId = if (currentEditingId != null) {
                currentEditingId!!
            } else {
                repository.createTemplate(name, null).toInt()
            }

            val entities = _selectedExercises.value.mapIndexed { index, item ->
                WorkoutExerciseEntity(
                    workoutId = workoutId,
                    exerciseId = item.exercise.id,
                    sets = item.sets,
                    reps = item.reps,
                    order = index
                )
            }

            repository.updateWorkout(workoutId, name, entities)
            clearSelection()
        }
    }

    private fun clearSelection() {
        _workoutName.value = ""
        _selectedExercises.value = emptyList()
        currentEditingId = null
    }

    fun loadWorkoutForEdit(workoutId: Int) {
        if (workoutId == -1) {
            clearSelection()
            return
        }

        viewModelScope.launch {
            val template = repository.getTemplateById(workoutId)

            val rawExercises = repository.getSavedWorkoutExercises(workoutId)

            if (template != null) {
                currentEditingId = workoutId
                _workoutName.value = template.name

                val uiList = mutableListOf<WorkoutExerciseUiState>()

                for (raw in rawExercises) {
                    val fullExercise = repository.getExerciseById(raw.exerciseId)

                    if (fullExercise != null) {
                        uiList.add(WorkoutExerciseUiState(
                            exercise = fullExercise,
                            sets = raw.sets,
                            reps = raw.reps
                        ))
                    }
                }

                _selectedExercises.value = uiList
            }
        }
    }

    fun clearDay(dayIndex: Int) { viewModelScope.launch { repository.clearDay(dayIndex) } }
    fun deleteWorkout(workoutId: Int) { viewModelScope.launch { repository.deleteWorkout(workoutId) } }
    fun onNameChange(newName: String) { _workoutName.value = newName }
    fun assignWorkoutToDay(dayIndex: Int, workoutId: Int, workoutName: String) {
        viewModelScope.launch {
            repository.setWorkoutToDay(dayIndex, workoutId, workoutName)
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