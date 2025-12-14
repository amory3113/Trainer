package com.example.trainer.GeneralScreen.Stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BmiGauge(bmi: Double) {
    // Ограничиваем значение для визуализации (от 15 до 40)
    val minBmi = 15.0
    val maxBmi = 40.0
    val progress = ((bmi - minBmi) / (maxBmi - minBmi)).coerceIn(0.0, 1.0).toFloat()

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            // Шкала
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2196F3), // Синий (мало)
                                Color(0xFF4CAF50), // Зеленый (Норма)
                                Color(0xFFFFC107), // Желтый (Избыток)
                                Color(0xFFF44336)  // Красный (Ожирение)
                            )
                        )
                    )
            )

            // Стрелка-указатель
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress) // Хак для позиционирования
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterEnd) // Стрелка в конце "невидимой" коробки
                        .offset(y = (-12).dp), // Чуть выше полоски
                    tint = Color.Black
                )
            }
        }
    }
}