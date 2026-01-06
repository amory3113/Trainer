package com.example.trainer.Logic

import com.example.trainer.Logic.Models.HealthResult
import com.example.trainer.Logic.Models.HealthStatus

object HealthAssessor {
    fun evaluate(
        chronic: Int,
        injuries: Int,
        heart: Int,
        restrictions: Int,
        fatigue: Int
    ): HealthResult {

        val totalScore = chronic + injuries + heart + restrictions + fatigue

        val status = when {
            totalScore <= 2 -> HealthStatus.GOOD
            totalScore <= 5 -> HealthStatus.MODERATE
            else -> HealthStatus.WEAK
        }

        val warning = when (status) {
            HealthStatus.GOOD -> null
            HealthStatus.MODERATE -> "Uwaga: Obowiązują umiarkowane ograniczenia. Wybierzemy delikatny ładunek."
            HealthStatus.WEAK -> "WAŻNE: Przed użyciem należy skonsultować się z lekarzem. Intensywność będzie zmniejszona."
        }

        return HealthResult(status, warning)
    }
}