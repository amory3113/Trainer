package com.example.trainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.produceState
import androidx.lifecycle.lifecycleScope // <-- Добавь этот импорт
import kotlinx.coroutines.launch
import com.example.trainer.data.AppDatabase
import com.example.trainer.data.UserRepository
import com.example.trainer.data.Exercise.ExerciseLoader // <-- Наш лоадер
import com.example.trainer.navigation.AppNavigation
import com.example.trainer.navigation.Routes
import com.example.trainer.ui.theme.TrainerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())

        val exerciseDao = database.exerciseDao()

        lifecycleScope.launch {
            val count = exerciseDao.getCount()
            if (count == 0) {
                println("DEBUG: База пустая, загружаем упражнения из JSON...")
                val exercises = ExerciseLoader.loadExercises(applicationContext)
                exerciseDao.insertAll(exercises)
                println("DEBUG: Загружено ${exercises.size} упражнений!")
            } else {
                println("DEBUG: Упражнения уже есть ($count шт). Пропускаем.")
            }
        }

        enableEdgeToEdge()
        setContent {
            TrainerTheme {
                val startRoute = produceState<String?>(initialValue = null) {
                    val user = repository.getUserProfile()
                    if (user != null) {
                        value = Routes.MAIN
                    } else {
                        value = Routes.WELCOME
                    }
                }

                if (startRoute.value == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AppNavigation(
                        repository = repository,
                        startDestination = startRoute.value!!
                    )
                }
            }
        }
    }
}