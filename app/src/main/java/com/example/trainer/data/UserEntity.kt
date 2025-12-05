package com.example.trainer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile") // Это имя таблицы внутри файла базы данных
data class UserEntity(
    // ID нужен базе, чтобы различать записи.
    // autoGenerate = true значит, что база сама поставит 1, потом 2 и т.д.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // --- Личные данные ---
    val gender: String,      // Храним как строку: "MALE" или "FEMALE"
    val age: Int,
    val weight: Double,
    val height: Double,

    // --- Цели и Активность ---
    val goal: String,           // Строка: "WEIGHT_LOSS" и т.д.
    val activityLevel: String,  // Строка: "BEGINNER" и т.д.

    // --- Здоровье ---
    val healthStatus: String,   // Строка: "GOOD", "WEAK"
    // Предупреждение может быть null, но в базе лучше хранить пустую строку или null
    val healthWarning: String?,

    // --- Тренировки ---
    val workoutLocation: String, // "HOME" или "GYM"
    val workoutFrequency: Int,   // Число: 3

    // --- План Питания (Развернули объект NutritionPlan) ---
    val targetCalories: Int,
    val proteinGrams: Int,
    val fatGrams: Int,
    val carbGrams: Int,

    // --- Дата создания (Полезно на будущее) ---
    val dateCreated: Long = System.currentTimeMillis()
)