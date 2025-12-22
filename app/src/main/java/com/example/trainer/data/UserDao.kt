package com.example.trainer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    // 1. Команда "Сохранить"
    // onConflict = REPLACE значит: если такой пользователь уже есть, замени его новым.
    // suspend значит: выполняй это в фоне, не тормози интерфейс.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // 2. Команда "Получить профиль"
    // Мы берем последнюю запись (ORDER BY id DESC LIMIT 1), так как у нас один пользователь.
    // Возвращает UserEntity? (может быть null, если база пустая).
    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    suspend fun getLastUser(): UserEntity?

    // 3. Команда "Сбросить прогресс" (Очистить таблицу)
    // Пригодится для кнопки "Начать заново" в настройках.
    @Query("DELETE FROM user_profile")
    suspend fun clearTable()

    // --- ИСТОРИЯ ВЕСА ---
    @Insert
    suspend fun insertWeight(weight: WeightEntity)

    // Получить всю историю (от старых к новым)
    @Query("SELECT * FROM weight_history ORDER BY date ASC")
    fun getAllWeightsFlow(): kotlinx.coroutines.flow.Flow<List<WeightEntity>>

    // --- НОВОЕ: ПИТАНИЕ ---

    // Ищем запись в промежутке времени (начало дня - конец дня)
    @Query("SELECT * FROM nutrition_history WHERE date >= :start AND date <= :end LIMIT 1")
    suspend fun getNutritionForDate(start: Long, end: Long): NutritionEntity?

    // Получить всю историю (для экрана статистики)
    @Query("SELECT * FROM nutrition_history ORDER BY date DESC")
    suspend fun getAllNutrition(): List<NutritionEntity>

    @Insert
    suspend fun insertNutrition(nutrition: NutritionEntity)

    @Update
    suspend fun updateNutrition(nutrition: NutritionEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Эта функция будет сама сообщать, если пользователь изменился
    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    fun getUserFlow(): kotlinx.coroutines.flow.Flow<UserEntity?>
}