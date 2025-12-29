package com.example.trainer.data.Exercise

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Это не таблица, это результат объединения двух таблиц
data class WorkoutWithExercises(
    @Embedded val template: WorkoutTemplateEntity,

    @Relation(
        parentColumn = "id",            // ID в таблице workout_templates
        entityColumn = "id",            // ID в таблице exercises
        associateBy = Junction(
            value = WorkoutExerciseEntity::class, // Таблица-посредник
            parentColumn = "workoutId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseEntity>
)