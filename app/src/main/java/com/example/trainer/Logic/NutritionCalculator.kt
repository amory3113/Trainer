package com.example.trainer.Logic

import com.example.trainer.Logic.Models.Gender
import com.example.trainer.Logic.Models.Goal
import com.example.trainer.Logic.Models.NutritionPlan
import com.example.trainer.Logic.Models.ActivityLevel

object NutritionCalculator {

    fun calculate(
        gender: Gender,
        weight: Double,
        height: Double,
        age: Int,
        activityLevel: ActivityLevel,
        goal: Goal
    ): NutritionPlan {

        val bmr = if (gender == Gender.MALE) {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }

        val activityMultiplier = when (activityLevel) {
            ActivityLevel.BEGINNER -> 1.2
            ActivityLevel.INTERMEDIATE -> 1.55
            ActivityLevel.ADVANCED -> 1.9
        }
        val tdee = bmr * activityMultiplier

        val targetCalories = when (goal) {
            Goal.WEIGHT_LOSS -> tdee * 0.8
            Goal.MUSCLE_GAIN -> tdee * 1.2
            Goal.MAINTAIN_FITNESS -> tdee * 1.0
        }

        val (pRatio, fRatio, cRatio) = when (goal) {
            Goal.WEIGHT_LOSS -> Triple(0.30, 0.30, 0.40)
            Goal.MUSCLE_GAIN -> Triple(0.25, 0.25, 0.50)
            Goal.MAINTAIN_FITNESS -> Triple(0.30, 0.30, 0.40)
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