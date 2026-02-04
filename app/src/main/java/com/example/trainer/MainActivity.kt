package com.example.trainer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts // <-- Важный импорт
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.produceState
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.trainer.data.AppDatabase
import com.example.trainer.data.UserRepository
import com.example.trainer.data.Exercise.ExerciseLoader
import com.example.trainer.navigation.AppNavigation
import com.example.trainer.navigation.Routes
import com.example.trainer.ui.theme.TrainerTheme
// Импорт нашего планировщика
import com.example.trainer.notification.NotificationScheduler
import com.example.trainer.notification.NotificationHelper

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("DEBUG: Разрешение на уведомления получено")
        } else {
            println("DEBUG: Разрешение отклонено")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())
        val exerciseDao = database.exerciseDao()

        NotificationHelper.createNotificationChannel(this)

        NotificationScheduler.scheduleAllNotifications(this)

        checkAndRequestNotificationPermission()

        lifecycleScope.launch {
            val count = exerciseDao.getCount()
            if (count == 0) {
                val exercises = ExerciseLoader.loadExercises(applicationContext)
                exerciseDao.insertAll(exercises)
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

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}