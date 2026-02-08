package com.example.trainer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    suspend fun getLastUser(): UserEntity?
    @Query("DELETE FROM user_profile")
    suspend fun clearTable()
    @Insert
    suspend fun insertWeight(weight: WeightEntity)
    @Query("SELECT * FROM weight_history ORDER BY date ASC")
    fun getAllWeightsFlow(): kotlinx.coroutines.flow.Flow<List<WeightEntity>>
    @Query("SELECT * FROM nutrition_history WHERE date >= :start AND date <= :end LIMIT 1")
    suspend fun getNutritionForDate(start: Long, end: Long): NutritionEntity?

    @Query("SELECT * FROM nutrition_history ORDER BY date DESC")
    suspend fun getAllNutrition(): List<NutritionEntity>

    // ДОБАВЛЯЕМ НОВУЮ (для живого обновления)
    @Query("SELECT * FROM nutrition_history ORDER BY date DESC")
    fun getAllNutritionFlow(): kotlinx.coroutines.flow.Flow<List<NutritionEntity>>

    @Insert
    suspend fun insertNutrition(nutrition: NutritionEntity)

    @Update
    suspend fun updateNutrition(nutrition: NutritionEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    fun getUserFlow(): kotlinx.coroutines.flow.Flow<UserEntity?>
}