package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.LightBlue
import com.example.trainer.ui.theme.TrainerTheme
import com.example.trainer.Logic.Models.Goal
import com.example.trainer.Logic.Models.WorkoutLocation
import kotlinx.coroutines.delay

@Composable
fun LoadScreen(
    viewModel: OnboardingViewModel? = null,
    onPlanReady: () -> Unit = {}
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // 2. "Слушаем" данные из ViewModel
    // collectAsState превращает поток данных в переменную, которая обновляет экран сама
    val nutritionPlan by viewModel?.nutritionPlan?.collectAsState() ?: remember { mutableStateOf(null) }
    val goal by viewModel?.userGoal?.collectAsState() ?: remember { mutableStateOf(null) }

    val frequency by viewModel?.workoutFrequency?.collectAsState() ?: remember { mutableIntStateOf(0) }
    val location by viewModel?.workoutLocation?.collectAsState() ?: remember { mutableStateOf(null) }

    val loadingTexts = listOf(
        "Анализируем ваши данные...",
        "Подбираем программу тренировок...",
        "Рассчитываем калорийность и БЖУ..."
    )

    LaunchedEffect(Unit) {
        // 3. ЗАПУСКАЕМ РАСЧЕТЫ!
        // Как только экран открылся, просим ViewModel посчитать всё
        viewModel?.calculateNutrition()
        for (i in loadingTexts.indices) {
            currentStep = i
            delay(1500) // 2 seconds for each step
        }
        isLoading = false
    }

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
        if (isLoading) {
            // Loading phase
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = Color(0xFF2196F3),
                    strokeWidth = 6.dp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = if (currentStep < loadingTexts.size) loadingTexts[currentStep] else loadingTexts.last(),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            // --- ЭКРАН РЕЗУЛЬТАТОВ ---

            // Используем Box, чтобы разнести контент (вверх) и кнопку (вниз)
            Box(modifier = Modifier.fillMaxSize()) {

                // Контент (Текст + Карточка)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Text(
                        text = "Ваш план готов!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "На основе ваших данных, мы подготовили для вас персональную программу.",
                        fontSize = 16.sp,
                        color = Color.Black,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Карточка с данными (Белый фон)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.9f), // Полупрозрачный белый
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Подготовка текста
                        val goalText = when(goal) {
                            Goal.WEIGHT_LOSS -> "Похудение"
                            Goal.MUSCLE_GAIN -> "Набор массы"
                            Goal.MAINTAIN_FITNESS -> "Поддержка формы"
                            null -> "Не определена"
                        }

                        val calories = nutritionPlan?.calories ?: 0
                        val p = nutritionPlan?.protein ?: 0
                        val f = nutritionPlan?.fat ?: 0
                        val c = nutritionPlan?.carbs ?: 0

                        val locationText = when(location) {
                            WorkoutLocation.HOME -> "Дома"
                            WorkoutLocation.GYM -> "Зал"
                            null -> ""
                        }

                        val trainingText = if (frequency > 0) "$frequency раза в неделю ($locationText)" else "-"

                        // Вывод строк
                        InfoRow(label = "Ваша цель:", value = goalText)
                        InfoRow(label = "Калории:", value = "$calories ккал")
                        InfoRow(label = "БЖУ:", value = "Б:$p / Ж:$f / У:$c (г)")
                        InfoRow(label = "Тренировки:", value = trainingText)
                    }
                }

                // Кнопка (Прижата к низу)
                Button(
                    onClick = { onPlanReady() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 26.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Показать мой план",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 17.sp, // Значение чуть крупнее
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadScreenPreview() {
    TrainerTheme {
        LoadScreen()
    }
}
