package com.example.trainer.data.Exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,           // Название: "День 1: Верх тела"
    val description: String? = null,
    val isSystemDefault: Boolean = false // Флаг: это наша созданная программа или пользовательская?
)