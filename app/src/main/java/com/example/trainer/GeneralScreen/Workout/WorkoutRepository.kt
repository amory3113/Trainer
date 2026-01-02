package com.example.trainer.data.Exercise

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // --- ПРОГРАММЫ ---
    val allTemplates: Flow<List<WorkoutWithExercises>> = workoutDao.getWorkoutsWithExercises()

    suspend fun createTemplate(name: String, description: String?): Long {
        val newTemplate = WorkoutTemplateEntity(name = name, description = description)
        return workoutDao.insertTemplate(newTemplate)
    }

    // --- РАСПИСАНИЕ ---
    val schedule: Flow<List<ScheduleEntity>> = workoutDao.getSchedule()

    // Назначить тренировку на день
    suspend fun setWorkoutToDay(dayOfWeek: Int, workoutId: Int, workoutName: String) {
        val item = ScheduleEntity(dayOfWeek, workoutId, workoutName)
        workoutDao.insertSchedule(item)
    }

    // Получить данные для редактирования
    suspend fun getTemplateById(id: Int) = workoutDao.getTemplateById(id)
    suspend fun getExercisesForTemplate(id: Int) = workoutDao.getExercisesForWorkoutList(id)

    suspend fun updateWorkout(workoutId: Int, newName: String, exercises: List<WorkoutExerciseEntity>) {
        // Используем нашу новую транзакцию
        workoutDao.updateWorkoutTransaction(workoutId, newName, exercises)
    }

    // Очистить день (сделать выходным)
    suspend fun clearDay(dayOfWeek: Int) {
        workoutDao.clearDay(dayOfWeek)
    }

    suspend fun deleteWorkout(workoutId: Int) {
        workoutDao.removeWorkoutFromSchedule(workoutId) // Сначала убираем из расписания
        workoutDao.deleteTemplateById(workoutId)        // Потом удаляем саму программу
    }

    suspend fun insertWorkoutExercise(entity: WorkoutExerciseEntity) {
        workoutDao.insertWorkoutExercise(entity)
    }


}