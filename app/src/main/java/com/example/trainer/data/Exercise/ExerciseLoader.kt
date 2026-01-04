package com.example.trainer.data.Exercise

import android.content.Context
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object ExerciseLoader {

    // Функция, которая читает JSON и возвращает список объектов
    fun loadExercises(context: Context): List<ExerciseEntity> {
        val exerciseList = mutableListOf<ExerciseEntity>()

        try {
            // 1. Открываем файл из assets
            val inputStream = context.assets.open("exercises.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()

            // 2. Разбираем текст как массив JSON
            val jsonArray = JSONArray(jsonString)

            // 3. Бежим по каждому элементу массива
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val exercise = ExerciseEntity(
                    name = item.getString("name"),
                    muscleGroup = item.getString("muscleGroup"),
                    imageName = item.getString("imageName"),
                    description = item.getString("description"),
                    // Если вдруг в JSON забыли поле upDown, подставим "UP" по умолчанию
                    upDown = item.optString("upDown", "UP"),
                    // Если поля нет, считаем что это тренажер (чтобы не сломать старое)
                    equipment = item.optString("equipment", "machine")
                )
                exerciseList.add(exercise)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return exerciseList
    }
}