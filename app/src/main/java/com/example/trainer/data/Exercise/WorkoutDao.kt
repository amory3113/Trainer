package com.example.trainer.data.Exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Transaction
    @Query("SELECT * FROM workout_templates")
    fun getWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExerciseEntity>)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteAllExercisesForTemplate(workoutId: Int)
    @Transaction
    suspend fun updateWorkoutTransaction(workoutId: Int, newName: String, exercises: List<WorkoutExerciseEntity>) {
        updateTemplateName(workoutId, newName)
        deleteAllExercisesForTemplate(workoutId)
        insertWorkoutExercises(exercises)
    }

    // --- ДОБАВЬ ВОТ ЭТУ ФУНКЦИЮ ---
    // Она достает именно настройки подходов и повторений для конкретной тренировки
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY `order` ASC")
    suspend fun getWorkoutExercisesRaw(workoutId: Int): List<WorkoutExerciseEntity>
    // -----------------------------

    @Query("UPDATE workout_templates SET name = :newName WHERE id = :workoutId")
    suspend fun updateTemplateName(workoutId: Int, newName: String)

    @Query("DELETE FROM workout_templates WHERE id = :workoutId")
    suspend fun deleteTemplateById(workoutId: Int)

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getTemplateById(id: Int): WorkoutTemplateEntity?

    @Query("""
        SELECT exercises.* FROM exercises 
        INNER JOIN workout_exercises ON exercises.id = workout_exercises.exerciseId
        WHERE workout_exercises.workoutId = :workoutId
        ORDER BY workout_exercises.`order` ASC
    """)
    suspend fun getExercisesForWorkoutList(workoutId: Int): List<ExerciseEntity>

    @Query("SELECT * FROM schedule ORDER BY dayOfWeek ASC")
    fun getSchedule(): Flow<List<ScheduleEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM schedule WHERE dayOfWeek = :day")
    suspend fun clearDay(day: Int)

    @Query("UPDATE schedule SET workoutId = NULL, workoutName = NULL WHERE workoutId = :workoutId")
    suspend fun removeWorkoutFromSchedule(workoutId: Int)

    @Query("SELECT * FROM schedule WHERE dayOfWeek = :day LIMIT 1")
    suspend fun getScheduleForDay(day: Int): ScheduleEntity?
}