package com.example.trainer.data.Exercise

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE // Удалим программу -> удалятся и упражнения из неё
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"]
        )
    ]
)
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: Int,         // Ссылка на программу
    val exerciseId: Int,        // Ссылка на упражнение
    val sets: Int = 3,          // Подходы по умолчанию
    val reps: Int = 10,         // Повторы по умолчанию
    val order: Int = 0          // Порядок упражнения в списке (1-е, 2-е...)
)