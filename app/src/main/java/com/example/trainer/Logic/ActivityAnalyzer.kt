package com.example.trainer.Logic

import com.example.trainer.Logic.Models.ActivityLevel // <--- Теперь мы его импортируем

object ActivityAnalyzer {

    fun analyze(
        jobScore: Int,
        sportFreqScore: Int,
        durationScore: Int,
        conditionScore: Int,
        fatigueScore: Int
    ): ActivityLevel {

        val totalScore = jobScore + sportFreqScore + durationScore + conditionScore + fatigueScore

        return when {
            totalScore >= 13 -> ActivityLevel.ADVANCED
            totalScore >= 8 -> ActivityLevel.INTERMEDIATE
            else -> ActivityLevel.BEGINNER
        }
    }
}