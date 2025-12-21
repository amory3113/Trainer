package com.example.trainer.GeneralScreen.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Состояние для показа диалога
    var showFoodDialog by remember { mutableStateOf(false) }

    GradientBackground {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Деталь А: Шапка
                HeaderSection()

                Spacer(modifier = Modifier.height(24.dp))

                // Деталь Б: Круг калорий
                CaloriesCircleSection(
                    calories = uiState.userProfile?.targetCalories ?: 0,
                    remainingCalories = viewModel.getCaloriesRemaining(),
                    progress = viewModel.getCaloriesProgress()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Деталь В: Панель БЖУ
                NutrientsSection(
                    proteinCurrent = uiState.proteinEaten,
                    proteinTarget = uiState.userProfile?.proteinGrams ?: 0,
                    proteinProgress = viewModel.getProteinProgress(),
                    fatCurrent = uiState.fatEaten,
                    fatTarget = uiState.userProfile?.fatGrams ?: 0,
                    fatProgress = viewModel.getFatProgress(),
                    carbsCurrent = uiState.carbsEaten,
                    carbsTarget = uiState.userProfile?.carbGrams ?: 0,
                    carbsProgress = viewModel.getCarbsProgress()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Деталь Г: Блок тренировки
                WorkoutSection()

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(80.dp))
                }
                FloatingActionButton(
                    onClick = { showFoodDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // В правом нижнем углу
                        .padding(16.dp),
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить еду", tint = Color.White)
                }
            }
        }
    }
    // ВЫЗОВ ДИАЛОГА
    if (showFoodDialog) {
        AddFoodDialog(
            onDismiss = { showFoodDialog = false },
            onConfirm = { k, p, f, c ->
                viewModel.addFood(k, p, f, c) // Вызываем новую функцию
                showFoodDialog = false
            }
        )
    }
}

@Composable
private fun HeaderSection() {
    Column {
        Text(
            text = getGreeting(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = getCurrentDate(),
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun CaloriesCircleSection(
    calories: Int,
    remainingCalories: Int,
    progress: Float
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressbar(
            calories = calories,
            remainingCalories = remainingCalories,
            progress = progress
        )
    }
}

@Composable
private fun NutrientsSection(
    proteinCurrent: Int,
    proteinTarget: Int,
    proteinProgress: Float,
    fatCurrent: Int,
    fatTarget: Int,
    fatProgress: Float,
    carbsCurrent: Int,
    carbsTarget: Int,
    carbsProgress: Float
) {
    Column {
        Text(
            text = "Нутриенты",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Используем Row, чтобы выстроить их в линию
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Отступ между столбиками
        ) {
            // Белки
            NutrientBar(
                name = "Белки",
                current = proteinCurrent,
                target = proteinTarget,
                progress = proteinProgress,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f) // Занимает 1/3 ширины
            )

            // Жиры
            NutrientBar(
                name = "Жиры",
                current = fatCurrent,
                target = fatTarget,
                progress = fatProgress,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )

            // Углеводы
            NutrientBar(
                name = "Углеводы",
                current = carbsCurrent,
                target = carbsTarget,
                progress = carbsProgress,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WorkoutSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Можно добавить иконку
            Text(
                text = "💪",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Тренировка на сегодня",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "День 1: Верх тела",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Не выполнено",
                    fontSize = 12.sp,
                    color = Color(0xFFFF5722)
                )
            }
        }
    }
}

@Composable
private fun AddFoodButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Добавить еду",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Добавить прием пищи (тест)",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Вспомогательные функции
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Доброе утро!"
        in 12..17 -> "Добрый день!"
        else -> "Добрый вечер!"
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
    return dateFormat.format(Date())
}