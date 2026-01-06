package com.example.trainer.data.Exercise

import android.content.Context
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object ExerciseLoader {
    fun loadExercises(context: Context): List<ExerciseEntity> {
        val exerciseList = mutableListOf<ExerciseEntity>()
        try {
            val inputStream = context.assets.open("exercises.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val exercise = ExerciseEntity(
                    name = item.getString("name"),
                    muscleGroup = item.getString("muscleGroup"),
                    imageName = item.getString("imageName"),
                    description = item.getString("description"),
                    upDown = item.optString("upDown", "UP"),
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