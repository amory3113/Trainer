package com.example.trainer.data

import com.example.trainer.Logic.Models.*
import java.util.Calendar

class UserRepository(private val userDao: UserDao) {

    // --- СОХРАНЕНИЕ ПРОФИЛЯ ---
    suspend fun saveUserProfile(
        gender: Gender,
        age: Int,
        weight: Double,
        height: Double,
        goal: Goal,
        activityLevel: ActivityLevel,
        healthResult: HealthResult?,
        workoutLocation: WorkoutLocation,
        workoutFrequency: Int,
        nutritionPlan: NutritionPlan
    ) {
        val userEntity = UserEntity(
            gender = gender.name,
            age = age,
            weight = weight,
            height = height,
            goal = goal.name,
            activityLevel = activityLevel.name,

            healthStatus = healthResult?.status?.name ?: "UNKNOWN",
            healthWarning = healthResult?.warningMessage,

            workoutLocation = workoutLocation.name,
            workoutFrequency = workoutFrequency,

            targetCalories = nutritionPlan.calories,
            proteinGrams = nutritionPlan.protein,
            fatGrams = nutritionPlan.fat,
            carbGrams = nutritionPlan.carbs
        )

        userDao.insertUser(userEntity)
        val initialWeight = WeightEntity(
            weight = weight.toFloat(),
            date = System.currentTimeMillis()
        )
        userDao.insertWeight(initialWeight)
    }

    // --- ВЕС ---
    suspend fun addWeightEntry(weight: Float) {
        val entry = WeightEntity(weight = weight, date = System.currentTimeMillis())
        userDao.insertWeight(entry)

        // Обновляем текущий вес в профиле
        val currentUser = userDao.getLastUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(weight = weight.toDouble())
            userDao.updateUser(updatedUser)
        }
    }

    fun getWeightHistoryFlow() = userDao.getAllWeightsFlow()

    // --- ПИТАНИЕ ---
    suspend fun getNutritionForDate(start: Long, end: Long): NutritionEntity? {
        return userDao.getNutritionForDate(start, end)
    }

    suspend fun addFood(kcal: Int, prot: Int, fat: Int, carb: Int) {
        val (start, end) = getDayRange()
        val todayEntry = userDao.getNutritionForDate(start, end)

        if (todayEntry != null) {
            val updatedEntry = todayEntry.copy(
                calories = todayEntry.calories + kcal,
                protein = todayEntry.protein + prot,
                fat = todayEntry.fat + fat,
                carbs = todayEntry.carbs + carb
            )
            userDao.updateNutrition(updatedEntry)
        } else {
            val newEntry = NutritionEntity(
                date = System.currentTimeMillis(),
                calories = kcal,
                protein = prot,
                fat = fat,
                carbs = carb
            )
            userDao.insertNutrition(newEntry)
        }
    }

    suspend fun getNutritionHistory(): List<NutritionEntity> {
        return userDao.getAllNutrition()
    }

    // ДОБАВЛЯЕМ НОВУЮ
    fun getNutritionHistoryStream(): kotlinx.coroutines.flow.Flow<List<NutritionEntity>> {
        return userDao.getAllNutritionFlow()
    }

    // --- ЮЗЕР ---
    val userFlow: kotlinx.coroutines.flow.Flow<UserEntity?> = userDao.getUserFlow()

    suspend fun getUserProfile(): UserEntity? {
        return userDao.getLastUser()
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    // --- !!! ВОТ ЭТА ФУНКЦИЯ, КОТОРОЙ НЕ ХВАТАЛО !!! ---
    suspend fun clearData() {
        userDao.clearTable()
    }
    // ---------------------------------------------------

    // Вспомогательная для времени
    private fun getDayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis

        return start to end
    }
}