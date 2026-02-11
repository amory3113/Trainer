package com.example.trainer.data.Exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val muscleGroup: String,
    val upDown: String,
    val imageName: String,
    val description: String,
    val equipment: String,
    val healthRisk: String = "SAFE"
)