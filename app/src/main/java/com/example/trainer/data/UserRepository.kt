package com.example.trainer.data

import com.example.trainer.Logic.Models.*

// Репозиторий скрывает от остального приложения то, как именно мы храним данные.
class UserRepository(private val userDao: UserDao) {

    // Функция, которая собирает ВСЕ данные и сохраняет их
    suspend fun saveUserProfile(
        gender: Gender,
        age: Int,
        weight: Double,
        height: Double,
        goal: Goal,
        activityLevel: ActivityLevel,
        healthResult: HealthResult?, // Может быть null
        workoutLocation: WorkoutLocation,
        workoutFrequency: Int,
        nutritionPlan: NutritionPlan
    ) {
        // 1. Превращаем сложные объекты в простые строки для базы
        val userEntity = UserEntity(
            gender = gender.name, // "MALE"
            age = age,
            weight = weight,
            height = height,
            goal = goal.name, // "WEIGHT_LOSS"
            activityLevel = activityLevel.name, // "BEGINNER"

            // Здоровье
            healthStatus = healthResult?.status?.name ?: "UNKNOWN",
            healthWarning = healthResult?.warningMessage,

            // Тренировки
            workoutLocation = workoutLocation.name, // "HOME"
            workoutFrequency = workoutFrequency,

            // План питания (раскладываем объект на цифры)
            targetCalories = nutritionPlan.calories,
            proteinGrams = nutritionPlan.protein,
            fatGrams = nutritionPlan.fat,
            carbGrams = nutritionPlan.carbs
        )

        // 2. Отправляем в базу
        userDao.insertUser(userEntity)
    }

    // Функция для получения профиля (для Главного экрана)
    suspend fun getUserProfile(): UserEntity? {
        return userDao.getLastUser()
    }

    // Очистка (для кнопки "Сброс")
    suspend fun clearData() {
        userDao.clearTable()
    }
}