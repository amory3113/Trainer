package com.example.trainer.Logic

import com.example.trainer.Logic.Models.Goal
import com.example.trainer.Logic.Models.WorkoutLocation
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.data.Exercise.WorkoutExerciseEntity
import com.example.trainer.data.Exercise.WorkoutTemplateEntity
import com.example.trainer.data.Exercise.WorkoutWithExercises
import com.example.trainer.data.UserEntity

object WorkoutGenerator {

    // Класс-результат, который мы вернем ViewModel-у
    data class GeneratedPlan(
        val templates: List<WorkoutTemplateEntity>,
        val exercisesMap: Map<Long, List<WorkoutExerciseEntity>>, // Long тут это tempId шаблона
        val schedule: Map<Int, Long> // День недели (1-7) -> tempId шаблона
    )

    fun generate(user: UserEntity, allExercises: List<ExerciseEntity>): GeneratedPlan {

        // 1. ФИЛЬТРАЦИЯ ПО ОБОРУДОВАНИЮ
        // Если Дом -> берем только свой вес и гантели. Если Зал -> берем всё.
        val validExercises = if (user.workoutLocation == WorkoutLocation.HOME.name) {
            allExercises.filter {
                it.equipment == "bodyweight" || it.equipment == "dumbbells"
            }
        } else {
            allExercises
        }

        // 2. ОПРЕДЕЛЕНИЕ ПАРАМЕТРОВ (Подходы / Повторы)
        val (sets, reps) = when (user.goal) {
            Goal.MUSCLE_GAIN.name -> 4 to 10
            Goal.WEIGHT_LOSS.name -> 3 to 15
            else -> 3 to 12 // MAINTAIN
        }

        // 3. ВЫБОР СПЛИТА
        // Если тренировок мало (<= 3) -> делаем Фулбади.
        // Если много (>= 4) -> делаем Верх/Низ.
        return if (user.workoutFrequency <= 3) {
            generateFullBodySplit(validExercises, sets, reps, user.workoutFrequency)
        } else {
            generateUpperLowerSplit(validExercises, sets, reps, user.workoutFrequency)
        }
    }

    // --- ГЕНЕРАТОР FULL BODY (Всё тело) ---
    private fun generateFullBodySplit(
        exercises: List<ExerciseEntity>,
        sets: Int,
        reps: Int,
        freq: Int
    ): GeneratedPlan {
        val templates = mutableListOf<WorkoutTemplateEntity>()
        val exercisesMap = mutableMapOf<Long, List<WorkoutExerciseEntity>>()
        val schedule = mutableMapOf<Int, Long>()

        // Создаем две вариации: Тренировка А и Тренировка Б
        val workoutA = createWorkoutTemplate(
            name = "Full Body A",
            exercises = selectExercisesForFullBody(exercises, variation = "A"),
            sets = sets,
            reps = reps,
            tempId = 1L
        )

        val workoutB = createWorkoutTemplate(
            name = "Full Body B",
            exercises = selectExercisesForFullBody(exercises, variation = "B"),
            sets = sets,
            reps = reps,
            tempId = 2L
        )

        templates.add(workoutA.first)
        exercisesMap[1L] = workoutA.second
        templates.add(workoutB.first)
        exercisesMap[2L] = workoutB.second

        // Расставляем по дням (Пн, Ср, Пт)
        // 1=Вс, 2=Пн, 3=Вт, 4=Ср, 5=Чт, 6=Пт, 7=Сб
        if (freq == 1) {
            schedule[2] = 1L // Пн
        } else if (freq == 2) {
            schedule[2] = 1L // Пн
            schedule[5] = 2L // Чт
        } else {
            schedule[2] = 1L // Пн
            schedule[4] = 2L // Ср
            schedule[6] = 1L // Пт
        }

        return GeneratedPlan(templates, exercisesMap, schedule)
    }

    // --- ГЕНЕРАТОР UPPER/LOWER (Верх / Низ) ---
    private fun generateUpperLowerSplit(
        exercises: List<ExerciseEntity>,
        sets: Int,
        reps: Int,
        freq: Int
    ): GeneratedPlan {
        val templates = mutableListOf<WorkoutTemplateEntity>()
        val exercisesMap = mutableMapOf<Long, List<WorkoutExerciseEntity>>()
        val schedule = mutableMapOf<Int, Long>()

        // 1. Тренировка ВЕРХ
        val upperExercises = exercises.filter {
            it.muscleGroup in listOf("CHEST", "BACK", "SHOULDERS", "ARMS")
        }.shuffled().take(7) // Берем 7 случайных упражнений на верх

        val workoutUpper = createWorkoutTemplate("Upper Body", upperExercises, sets, reps, 10L)

        // 2. Тренировка НИЗ + ПРЕСС
        val lowerExercises = exercises.filter {
            it.muscleGroup in listOf("LEGS", "ABS", "CARDIO")
        }.shuffled().take(6)

        val workoutLower = createWorkoutTemplate("Lower Body", lowerExercises, sets, reps, 20L)

        templates.add(workoutUpper.first)
        exercisesMap[10L] = workoutUpper.second
        templates.add(workoutLower.first)
        exercisesMap[20L] = workoutLower.second

        // Расписание: Пн(Верх), Вт(Низ), Чт(Верх), Пт(Низ)
        schedule[2] = 10L
        schedule[3] = 20L
        schedule[5] = 10L
        schedule[6] = 20L

        return GeneratedPlan(templates, exercisesMap, schedule)
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ---

    private fun createWorkoutTemplate(
        name: String,
        exercises: List<ExerciseEntity>,
        sets: Int,
        reps: Int,
        tempId: Long // Временный ID для связки внутри генератора
    ): Pair<WorkoutTemplateEntity, List<WorkoutExerciseEntity>> {

        // Тут мы ставим id = 0, так как реальный ID выдаст база данных при вставке.
        // Но нам нужно как-то связать их в GeneratedPlan, поэтому используем tempId во внешней мапе.
        val template = WorkoutTemplateEntity(name = name, description = "System generated", isSystemDefault = false)

        val workoutExercises = exercises.mapIndexed { index, exercise ->
            WorkoutExerciseEntity(
                workoutId = 0, // Будет заменено при сохранении
                exerciseId = exercise.id,
                sets = sets,
                reps = reps,
                order = index
            )
        }
        return template to workoutExercises
    }

    // Логика подбора упражнений для Фулбади
    private fun selectExercisesForFullBody(all: List<ExerciseEntity>, variation: String): List<ExerciseEntity> {
        // Мы пытаемся взять по 1 упражнению на каждую группу
        val result = mutableListOf<ExerciseEntity>()

        val groups = listOf("LEGS", "CHEST", "BACK", "SHOULDERS", "ARMS", "ABS")

        groups.forEach { group ->
            val candidates = all.filter { it.muscleGroup == group }
            if (candidates.isNotEmpty()) {
                // Если вариация А -> берем первое, Б -> берем второе (или последнее)
                // Это простая логика, чтобы тренировки отличались
                val exercise = if (variation == "A") candidates.first() else candidates.last()
                result.add(exercise)
            }
        }
        return result
    }
}