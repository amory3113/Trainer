package com.example.trainer.data.Exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey val dayOfWeek: Int,
    val workoutId: Int?,
    val workoutName: String?
)