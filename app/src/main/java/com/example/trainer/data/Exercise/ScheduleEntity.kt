package com.example.trainer.data.Exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey val dayOfWeek: Int, // 1 = Понедельник, 7 = Воскресенье
    val workoutId: Int?,            // Ссылка на программу (null = выходной)
    val workoutName: String?        // Копия названия, чтобы быстрее отображать
)