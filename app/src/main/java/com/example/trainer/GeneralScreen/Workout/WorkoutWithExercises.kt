package com.example.trainer.data.Exercise

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
data class WorkoutWithExercises(
    @Embedded val template: WorkoutTemplateEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseEntity::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseEntity>
)