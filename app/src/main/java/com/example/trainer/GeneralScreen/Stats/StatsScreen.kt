package com.example.trainer.GeneralScreen.Stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.trainer.ui.theme.GradientBackground

@Composable
fun StatsScreen() {
    GradientBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Статистика и Прогресс")
        }
    }
}