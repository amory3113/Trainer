package com.example.trainer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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
    suspend fun getAllWeights(): List<WeightEntity>

    // Получить последний вес (для обновления профиля)
    @Query("SELECT * FROM weight_history ORDER BY date DESC LIMIT 1")
    suspend fun getLastWeight(): WeightEntity?
}