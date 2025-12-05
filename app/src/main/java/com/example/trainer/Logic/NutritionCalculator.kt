package com.example.trainer.Logic

import com.example.trainer.Logic.Models.Gender
import com.example.trainer.Logic.Models.Goal
import com.example.trainer.Logic.Models.NutritionPlan
import com.example.trainer.Logic.Models.ActivityLevel

object NutritionCalculator {

    fun calculate(
        gender: Gender,
        weight: Double, // кг
        height: Double, // см
        age: Int,
        activityLevel: ActivityLevel,
        goal: Goal
    ): NutritionPlan {

        // 1. Считаем BMR (Формула Миффлина-Сан Жеора)
        val bmr = if (gender == Gender.MALE) {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }

        // 2. Учитываем активность (TDEE)
        // BMR * Коэффициент активности
        val activityMultiplier = when (activityLevel) {
            ActivityLevel.BEGINNER -> 1.2
            ActivityLevel.INTERMEDIATE -> 1.55
            ActivityLevel.ADVANCED -> 1.9
        }
        val tdee = bmr * activityMultiplier

        // 3. Корректировка на Цель
        val targetCalories = when (goal) {
            Goal.WEIGHT_LOSS -> tdee * 0.8    // Дефицит 20%
            Goal.MUSCLE_GAIN -> tdee * 1.2    // Профицит 20%
            Goal.MAINTAIN_FITNESS -> tdee * 1.0
        }

        // 4. Расчет БЖУ (в граммах)
        // Белок = 4 ккал, Жир = 9 ккал, Углеводы = 4 ккал
        val (pRatio, fRatio, cRatio) = when (goal) {
            Goal.WEIGHT_LOSS -> Triple(0.30, 0.30, 0.40)      // 30/30/40
            Goal.MUSCLE_GAIN -> Triple(0.25, 0.25, 0.50)      // 25/25/50
            Goal.MAINTAIN_FITNESS -> Triple(0.30, 0.30, 0.40) // 30/30/40
        }

        val proteinGrams = (targetCalories * pRatio / 4).toInt()
        val fatGrams = (targetCalories * fRatio / 9).toInt()
        val carbGrams = (targetCalories * cRatio / 4).toInt()

        return NutritionPlan(
            calories = targetCalories.toInt(),
            protein = proteinGrams,
            fat = fatGrams,
            carbs = carbGrams
        )
    }
}