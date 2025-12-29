package com.example.trainer.data.Exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // --- РАБОТА С ШАБЛОНАМИ (ПРОГРАММАМИ) ---

    @androidx.room.Transaction // Важно! Гарантирует целостность сборки данных
    @Query("SELECT * FROM workout_templates")
    fun getWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    // Удалить программу по ID
    @Query("DELETE FROM workout_templates WHERE id = :workoutId")
    suspend fun deleteTemplateById(workoutId: Int)

    // Очистить расписание от этой программы (где она была назначена)
    @Query("UPDATE schedule SET workoutId = NULL, workoutName = NULL WHERE workoutId = :workoutId")
    suspend fun removeWorkoutFromSchedule(workoutId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long // Возвращает ID новой программы

    // Добавить упражнение в программу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    // Получить упражнения конкретной программы
    // Мы "джойним" таблицу связей с таблицей упражнений, чтобы сразу получить названия и картинки
    @Query("""
        SELECT * FROM exercises 
        INNER JOIN workout_exercises ON exercises.id = workout_exercises.exerciseId
        WHERE workout_exercises.workoutId = :workoutId
        ORDER BY workout_exercises.`order` ASC
    """)
    fun getExercisesForWorkout(workoutId: Int): Flow<List<ExerciseEntity>>
    // Примечание: Это простой вариант. Если нам нужны подходы/повторы, придется делать сложнее класс-обертку.
    // Пока оставим так для простоты отображения списка.

    // --- РАБОТА С РАСПИСАНИЕМ ---

    @Query("SELECT * FROM schedule ORDER BY dayOfWeek ASC")
    fun getSchedule(): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    // Очистить день (сделать выходным)
    @Query("DELETE FROM schedule WHERE dayOfWeek = :day")
    suspend fun clearDay(day: Int)
}