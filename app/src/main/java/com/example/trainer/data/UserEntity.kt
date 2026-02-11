package com.example.trainer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gender: String,
    val age: Int,
    val weight: Double,
    val height: Double,

    val goal: String,
    val activityLevel: String,

    val hasHeartIssues: Boolean,
    val hasJointIssues: Boolean,

    val healthStatus: String,
    val healthWarning: String?,

    val workoutLocation: String,
    val workoutFrequency: Int,

    val targetCalories: Int,
    val proteinGrams: Int,
    val fatGrams: Int,
    val carbGrams: Int,

    val dateCreated: Long = System.currentTimeMillis()
)