package com.example.trainer.data.Exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    // Получить все упражнения (для списка выбора)
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    // Вставить список упражнений (из JSON при первом запуске)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    // Проверка: пустая база или нет? (Чтобы знать, надо ли грузить JSON)
    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int
}