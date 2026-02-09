package com.example.trainer.Logic

import com.example.trainer.Logic.Models.Goal
import com.example.trainer.Logic.Models.WorkoutLocation
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.data.Exercise.WorkoutExerciseEntity
import com.example.trainer.data.Exercise.WorkoutTemplateEntity

object WorkoutGenerator {

    data class GeneratedWorkout(
        val tempId: Long,
        val template: WorkoutTemplateEntity,
        val exercises: List<WorkoutExerciseEntity>
    )

    data class GeneratedPlan(
        val workouts: List<GeneratedWorkout>,
        val schedule: Map<Int, Long>
    )

    fun generate(user: com.example.trainer.data.UserEntity, allExercises: List<ExerciseEntity>): GeneratedPlan {

        val validExercises = if (user.workoutLocation == WorkoutLocation.HOME.name) {
            allExercises.filter { it.equipment == "bodyweight" || it.equipment == "dumbbells" }
        } else {
            allExercises
        }

        val (sets, reps) = when (user.goal) {
            Goal.MUSCLE_GAIN.name -> 4 to 10
            Goal.WEIGHT_LOSS.name -> 3 to 15
            else -> 3 to 12
        }

        return when {
            user.workoutFrequency <= 3 -> generateFullBodySplit(validExercises, sets, reps, user.workoutFrequency)
            user.workoutFrequency == 4 -> generateUpperLowerSplit(validExercises, sets, reps)
            else -> generateBodyPartSplit(validExercises, sets, reps)
        }
    }

    private fun generateFullBodySplit(
        exercises: List<ExerciseEntity>, sets: Int, reps: Int, freq: Int
    ): GeneratedPlan {
        val workouts = mutableListOf<GeneratedWorkout>()
        val schedule = mutableMapOf<Int, Long>()

        val wA = createWorkout("Full Body A", selectExercisesForFullBody(exercises, "A"), sets, reps)
        val wB = createWorkout("Full Body B", selectExercisesForFullBody(exercises, "B"), sets, reps)

        workouts.add(GeneratedWorkout(1L, wA.first, wA.second))
        workouts.add(GeneratedWorkout(2L, wB.first, wB.second))

        if (freq == 1) {
            schedule[0] = 1L
        } else if (freq == 2) {
            schedule[0] = 1L; schedule[3] = 2L
        } else {
            schedule[0] = 1L; schedule[2] = 2L; schedule[4] = 1L
        }

        return GeneratedPlan(workouts, schedule)
    }

    private fun generateUpperLowerSplit(
        exercises: List<ExerciseEntity>, sets: Int, reps: Int
    ): GeneratedPlan {
        val workouts = mutableListOf<GeneratedWorkout>()
        val schedule = mutableMapOf<Int, Long>()

        val upperEx = exercises.filter { it.muscleGroup in listOf("CHEST", "BACK", "SHOULDERS", "ARMS") }.shuffled().take(7)
        val lowerEx = exercises.filter { it.muscleGroup in listOf("LEGS", "ABS", "CARDIO") }.shuffled().take(6)

        val wUp = createWorkout("Upper Body", upperEx, sets, reps)
        val wLow = createWorkout("Lower Body", lowerEx, sets, reps)

        workouts.add(GeneratedWorkout(10L, wUp.first, wUp.second))
        workouts.add(GeneratedWorkout(20L, wLow.first, wLow.second))

        schedule[0] = 10L; schedule[1] = 20L; schedule[3] = 10L; schedule[4] = 20L

        return GeneratedPlan(workouts, schedule)
    }

    private fun generateBodyPartSplit(
        exercises: List<ExerciseEntity>, sets: Int, reps: Int
    ): GeneratedPlan {
        val workouts = mutableListOf<GeneratedWorkout>()
        val schedule = mutableMapOf<Int, Long>()

        val wChest = createWorkout("Klatka Piersiowa", exercises.filter { it.muscleGroup == "CHEST" }.shuffled().take(5), sets, reps)
        val wBack = createWorkout("Plecy", exercises.filter { it.muscleGroup == "BACK" }.shuffled().take(5), sets, reps)
        val wLegs = createWorkout("Nogi i Brzuch", exercises.filter { it.muscleGroup == "LEGS" || it.muscleGroup == "ABS" }.shuffled().take(6), sets, reps)
        val wShoulders = createWorkout("Barki", exercises.filter { it.muscleGroup == "SHOULDERS" }.shuffled().take(5), sets, reps)
        val wArms = createWorkout("Ramiona", exercises.filter { it.muscleGroup == "ARMS" }.shuffled().take(6), sets, reps)

        workouts.add(GeneratedWorkout(31L, wChest.first, wChest.second))
        workouts.add(GeneratedWorkout(32L, wBack.first, wBack.second))
        workouts.add(GeneratedWorkout(33L, wLegs.first, wLegs.second))
        workouts.add(GeneratedWorkout(34L, wShoulders.first, wShoulders.second))
        workouts.add(GeneratedWorkout(35L, wArms.first, wArms.second))

        schedule[0] = 31L
        schedule[1] = 32L
        schedule[2] = 33L
        schedule[3] = 34L
        schedule[4] = 35L

        return GeneratedPlan(workouts, schedule)
    }

    private fun createWorkout(name: String, exercises: List<ExerciseEntity>, sets: Int, reps: Int): Pair<WorkoutTemplateEntity, List<WorkoutExerciseEntity>> {
        val template = WorkoutTemplateEntity(name = name, description = "System generated", isSystemDefault = false)
        val workoutExercises = exercises.mapIndexed { index, exercise ->
            WorkoutExerciseEntity(workoutId = 0, exerciseId = exercise.id, sets = sets, reps = reps, order = index)
        }
        return template to workoutExercises
    }

    private fun selectExercisesForFullBody(all: List<ExerciseEntity>, variation: String): List<ExerciseEntity> {
        val result = mutableListOf<ExerciseEntity>()
        listOf("LEGS", "CHEST", "BACK", "SHOULDERS", "ARMS", "ABS").forEach { group ->
            val candidates = all.filter { it.muscleGroup == group }
            if (candidates.isNotEmpty()) result.add(if (variation == "A") candidates.first() else candidates.last())
        }
        return result
    }
}