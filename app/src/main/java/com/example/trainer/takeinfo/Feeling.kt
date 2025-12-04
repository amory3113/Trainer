package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.Logic.Models.WorkoutLocation
import com.example.trainer.ui.theme.LightBlue
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun Feeling(
    viewModel: OnboardingViewModel? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    // Храним сразу в правильных типах данных
    var selectedLocation by remember { mutableStateOf<WorkoutLocation?>(null) }
    var selectedFrequency by remember { mutableStateOf<Int?>(null) } // Храним число

    // Проверка: можно идти дальше, только если всё выбрано
    val isFormComplete = selectedLocation != null && selectedFrequency != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBlue,
                        Color.White
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Предпочтения",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Location preference
            Text(
                text = "Где вам удобнее заниматься?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Кнопка ДОМА
                LocationButton(
                    text = "Дома",
                    isSelected = selectedLocation == WorkoutLocation.HOME,
                    onClick = { selectedLocation = WorkoutLocation.HOME }
                )

                // Кнопка ЗАЛ
                LocationButton(
                    text = "В зале",
                    isSelected = selectedLocation == WorkoutLocation.GYM,
                    onClick = { selectedLocation = WorkoutLocation.GYM }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Training frequency
            Text(
                text = "Сколько раз в неделю вы готовы тренироваться?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FreqButton("2 раза", 2, selectedFrequency) { selectedFrequency = 2 }
                    FreqButton("3 раза", 3, selectedFrequency) { selectedFrequency = 3 }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FreqButton("4 раза", 4, selectedFrequency) { selectedFrequency = 4 }
                    FreqButton("5+ раз", 5, selectedFrequency) { selectedFrequency = 5 }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 26.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onBackClick() },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = "Назад",
                    color = Color(0xFF2196F3),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = { if (isFormComplete) {
                    viewModel?.saveWorkoutPreferences(
                        location = selectedLocation!!,
                        frequency = selectedFrequency!!
                    )
                    onNextClick()
                } },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Рассчитать мой план",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RowScope.LocationButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun RowScope.FreqButton(text: String, value: Int, selectedValue: Int?, onClick: () -> Unit) {
    val isSelected = selectedValue == value
    Box(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeelingPreview() {
    TrainerTheme {
        Feeling(onNextClick = {})
    }
}
