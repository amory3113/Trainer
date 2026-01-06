package com.example.trainer.Logic.Models

enum class HealthStatus {
    GOOD,
    MODERATE,
    WEAK
}
data class HealthResult(
    val status: HealthStatus,
    val warningMessage: String? = null
)