package com.example.trainer.data.Exercise

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao, private val exerciseDao: ExerciseDao) {

    val allTemplates: Flow<List<WorkoutWithExercises>> = workoutDao.getWorkoutsWithExercises()

    suspend fun createTemplate(name: String, description: String?): Long {
        val newTemplate = WorkoutTemplateEntity(name = name, description = description)
        return workoutDao.insertTemplate(newTemplate)
    }
    val schedule: Flow<List<ScheduleEntity>> = workoutDao.getSchedule()
    suspend fun setWorkoutToDay(dayOfWeek: Int, workoutId: Int, workoutName: String) {
        val item = ScheduleEntity(dayOfWeek, workoutId, workoutName)
        workoutDao.insertSchedule(item)
    }
    suspend fun getTemplateById(id: Int) = workoutDao.getTemplateById(id)


    suspend fun getSavedWorkoutExercises(workoutId: Int): List<WorkoutExerciseEntity> {
        return workoutDao.getWorkoutExercisesRaw(workoutId)
    }
    fun getWorkoutExercisesStream(workoutId: Int): Flow<List<WorkoutExerciseEntity>> {
        return workoutDao.getWorkoutExercisesFlow(workoutId)
    }

    suspend fun getExerciseById(id: Int) = exerciseDao.getExerciseById(id)

    suspend fun updateWorkout(workoutId: Int, newName: String, exercises: List<WorkoutExerciseEntity>) {
        workoutDao.updateWorkoutTransaction(workoutId, newName, exercises)
    }
    suspend fun clearDay(dayOfWeek: Int) {
        workoutDao.clearDay(dayOfWeek)
    }
    suspend fun deleteWorkout(workoutId: Int) {
        workoutDao.removeWorkoutFromSchedule(workoutId)
        workoutDao.deleteTemplateById(workoutId)
    }
}