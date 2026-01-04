package com.example.trainer.data.Exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,           // Название: "Жим лежа"
    val muscleGroup: String,    // Группа мышц: "Грудь", "Спина" (возьмем из Enums)
    val upDown: String, // верх и низ тела
    val imageName: String,      // Имя картинки в ресурсах: "bench_press_img"
    val description: String,     // Описание техники
    val equipment: String
)