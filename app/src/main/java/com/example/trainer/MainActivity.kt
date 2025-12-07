package com.example.trainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.trainer.data.AppDatabase
import com.example.trainer.data.UserRepository
import com.example.trainer.navigation.AppNavigation
import com.example.trainer.ui.theme.TrainerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = UserRepository(database.userDao())

        enableEdgeToEdge()
        setContent {
            TrainerTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}