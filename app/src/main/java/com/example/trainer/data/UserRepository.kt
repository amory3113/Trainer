package com.example.trainer.data

import com.example.trainer.Logic.Models.*
import java.util.Calendar

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

        val initialWeight = WeightEntity(
            weight = weight.toFloat(),
            date = System.currentTimeMillis()
        )
        userDao.insertWeight(initialWeight)
    }


    // Добавить новый вес
    suspend fun addWeightEntry(weight: Float) {
        val entry = WeightEntity(weight = weight, date = System.currentTimeMillis())
        userDao.insertWeight(entry)

        // Также обновляем текущий вес в профиле пользователя!
        val currentUser = userDao.getLastUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(weight = weight.toDouble())
            userDao.insertUser(updatedUser)
        }
    }

    suspend fun getWeightHistory(): List<WeightEntity> {
        return userDao.getAllWeights()
    }

    // Функция для получения профиля (для Главного экрана)
    suspend fun getUserProfile(): UserEntity? {
        return userDao.getLastUser()
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    // Очистка (для кнопки "Сброс")
    suspend fun clearData() {
        userDao.clearTable()
    }

    // Получить питание за СЕГОДНЯ
    suspend fun getTodayNutrition(): NutritionEntity? {
        val (start, end) = getDayRange()
        return userDao.getNutritionForDate(start, end)
    }

    // Добавить еду (вызывается с Главного экрана)
    suspend fun addFood(kcal: Int, prot: Int, fat: Int, carb: Int) {
        val (start, end) = getDayRange()

        // 1. Ищем запись за сегодня
        val todayEntry = userDao.getNutritionForDate(start, end)

        if (todayEntry != null) {
            // 2. Если есть - обновляем (суммируем)
            val updatedEntry = todayEntry.copy(
                calories = todayEntry.calories + kcal,
                protein = todayEntry.protein + prot,
                fat = todayEntry.fat + fat,
                carbs = todayEntry.carbs + carb
            )
            userDao.updateNutrition(updatedEntry)
        } else {
            // 3. Если нет - создаем новую
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

    // Получить всю историю (для экрана Истории)
    suspend fun getNutritionHistory(): List<NutritionEntity> {
        return userDao.getAllNutrition()
    }

    // Вспомогательная функция для получения начала и конца текущего дня
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

        return Pair(start, end)
    }
}