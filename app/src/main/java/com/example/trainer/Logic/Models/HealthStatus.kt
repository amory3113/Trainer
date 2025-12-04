package com.example.trainer.Logic.Models

enum class HealthStatus {
    GOOD,       // Здоров
    MODERATE,   // Умеренно
    WEAK        // Слабое (нужен врач)
}

// Класс для хранения результата: статус + текст предупреждения
data class HealthResult(
    val status: HealthStatus,
    val warningMessage: String? = null
)