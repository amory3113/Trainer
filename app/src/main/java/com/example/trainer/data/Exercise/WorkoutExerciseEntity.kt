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
            onDelete = ForeignKey.CASCADE
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
    val workoutId: Int,
    val exerciseId: Int,
    val sets: Int = 3,
    val reps: Int = 10,
    val order: Int = 0
)