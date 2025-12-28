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

        // Получаем доступ к новому DAO упражнений
        val exerciseDao = database.exerciseDao()

        // --- ЗАГРУЗКА УПРАЖНЕНИЙ (Один раз при первом старте) ---
        lifecycleScope.launch {
            // 1. Спрашиваем базу: сколько там упражнений?
            val count = exerciseDao.getCount()

            // 2. Если 0 (база пустая), значит это первый запуск
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
                // 1. Проверяем состояние пользователя асинхронно
                // initialValue = null, значит мы еще не знаем, куда идти
                val startRoute = produceState<String?>(initialValue = null) {
                    // Этот код выполняется в фоновом потоке
                    val user = repository.getUserProfile()
                    if (user != null) {
                        value = Routes.MAIN // Если пользователь есть -> Главный экран
                    } else {
                        value = Routes.WELCOME // Если нет -> Онбординг
                    }
                }

                // 2. Логика отображения
                if (startRoute.value == null) {
                    // ПОКА ГРУЗИМСЯ: Показываем крутилку или пустой экран
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // ЗАГРУЗИЛИСЬ: Запускаем навигацию с правильной точки
                    AppNavigation(
                        repository = repository,
                        startDestination = startRoute.value!! // Передаем вычисленный маршрут
                    )
                }
            }
        }
    }
}