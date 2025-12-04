package com.example.trainer.Logic

import com.example.trainer.Logic.Models.HealthResult
import com.example.trainer.Logic.Models.HealthStatus

object HealthAssessor {

    /**
     * Считает здоровье.
     * 0 баллов = Нет проблем
     * 1 балл = Мелкие проблемы
     * 3 балла = Серьезные проблемы
     */
    fun evaluate(
        chronic: Int,
        injuries: Int,
        heart: Int,
        restrictions: Int,
        fatigue: Int
    ): HealthResult {

        // 1. Складываем все баллы
        val totalScore = chronic + injuries + heart + restrictions + fatigue

        // 2. Определяем статус
        val status = when {
            totalScore <= 2 -> HealthStatus.GOOD
            totalScore <= 5 -> HealthStatus.MODERATE
            else -> HealthStatus.WEAK
        }

        // 3. Формируем текст предупреждения (если нужно)
        val warning = when (status) {
            HealthStatus.GOOD -> null
            HealthStatus.MODERATE -> "Внимание: Умеренные ограничения. Мы подберем щадящую нагрузку."
            HealthStatus.WEAK -> "ВАЖНО: Рекомендуется консультация врача. Интенсивность будет снижена."
        }

        return HealthResult(status, warning)
    }
}