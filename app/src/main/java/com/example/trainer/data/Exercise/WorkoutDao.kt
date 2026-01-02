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

    @Transaction
    @Query("SELECT * FROM workout_templates")
    fun getWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    // --- РАБОТА С УПРАЖНЕНИЯМИ ВНУТРИ ПРОГРАММЫ ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    // Вставка списка упражнений (для оптимизации)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExerciseEntity>)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteAllExercisesForTemplate(workoutId: Int)

    // Транзакция для полного обновления тренировки
    @Transaction
    suspend fun updateWorkoutTransaction(workoutId: Int, newName: String, exercises: List<WorkoutExerciseEntity>) {
        updateTemplateName(workoutId, newName)
        deleteAllExercisesForTemplate(workoutId)
        insertWorkoutExercises(exercises)
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ ЗАПРОСЫ ---

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

    // --- РАБОТА С РАСПИСАНИЕМ ---

    @Query("SELECT * FROM schedule ORDER BY dayOfWeek ASC")
    fun getSchedule(): Flow<List<ScheduleEntity>>

    // ВОТ ЭТОГО МЕТОДА НЕ ХВАТАЛО:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM schedule WHERE dayOfWeek = :day")
    suspend fun clearDay(day: Int)

    @Query("UPDATE schedule SET workoutId = NULL, workoutName = NULL WHERE workoutId = :workoutId")
    suspend fun removeWorkoutFromSchedule(workoutId: Int)
}