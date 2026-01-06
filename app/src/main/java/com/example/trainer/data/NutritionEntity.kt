package com.example.trainer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_history")
data class NutritionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int
)