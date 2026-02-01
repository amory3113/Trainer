package com.example.trainer.GeneralScreen.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Weekend
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
//                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                HeaderSection(dayName = viewModel.getTodayName())

                Spacer(modifier = Modifier.height(24.dp))

                CaloriesCircleSection(
                    calories = uiState.userProfile?.targetCalories ?: 0,
                    remainingCalories = viewModel.getCaloriesRemaining(),
                    progress = viewModel.getCaloriesProgress()
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                WorkoutSection(workoutName = uiState.todayWorkoutName)

                Spacer(modifier = Modifier.height(24.dp))

                Spacer(modifier = Modifier.height(80.dp))
                }
                FloatingActionButton(
                    onClick = { showFoodDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj jedzenie", tint = Color.White)
                }
            }
        }
    }
    if (showFoodDialog) {
        AddFoodDialog(
            onDismiss = { showFoodDialog = false },
            onConfirm = { k, p, f, c ->
                viewModel.addFood(k, p, f, c)
                showFoodDialog = false
            }
        )
    }
}

@Composable
private fun HeaderSection(dayName: String) {
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
            text = "Składniki odżywcze",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NutrientBar(
                name = "Białka",
                current = proteinCurrent,
                target = proteinTarget,
                progress = proteinProgress,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            )

            NutrientBar(
                name = "Tłuszcze",
                current = fatCurrent,
                target = fatTarget,
                progress = fatProgress,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )

            NutrientBar(
                name = "Węglow.",
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
private fun WorkoutSection(workoutName: String?) {
    if (workoutName != null) {
        ActiveWorkoutCard(name = workoutName)
    } else {
        RestDayCard()
    }
}

@Composable
fun ActiveWorkoutCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Zgodnie z planem:", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun RestDayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Weekend,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = "Dzień wolny", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Odpoczynek jest ważny dla wzrostu mięśni.", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Dzień dobry!"
        in 12..17 -> "Witaj!"
        else -> "Dobry wieczór!"
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("pl", "PL"))
    val rawDate = dateFormat.format(Date())
    return rawDate.replaceFirstChar { it.uppercase() }
}