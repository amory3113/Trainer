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

    val nutritionPlan by viewModel?.nutritionPlan?.collectAsState() ?: remember { mutableStateOf(null) }
    val goal by viewModel?.userGoal?.collectAsState() ?: remember { mutableStateOf(null) }

    val frequency by viewModel?.workoutFrequency?.collectAsState() ?: remember { mutableIntStateOf(0) }
    val location by viewModel?.workoutLocation?.collectAsState() ?: remember { mutableStateOf(null) }

    val loadingTexts = listOf(
        "Analizowanie danych...",
        "Wybieramy program szkoleniowy...",
        "Obliczanie kalorii i BJU..."
    )

    LaunchedEffect(Unit) {
        viewModel?.calculateNutrition()
        for (i in loadingTexts.indices) {
            currentStep = i
            delay(1500)
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
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Text(
                        text = "Twój plan jest gotowy!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Na podstawie Twoich danych przygotowaliśmy dla Ciebie spersonalizowany program.",
                        fontSize = 16.sp,
                        color = Color.Black,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val goalText = when(goal) {
                            Goal.WEIGHT_LOSS -> "Utrata wagi"
                            Goal.MUSCLE_GAIN -> "Przyrost masy"
                            Goal.MAINTAIN_FITNESS -> "Utrzymanie formy"
                            null -> "Nie zdefiniowano"
                        }

                        val calories = nutritionPlan?.calories ?: 0
                        val p = nutritionPlan?.protein ?: 0
                        val f = nutritionPlan?.fat ?: 0
                        val c = nutritionPlan?.carbs ?: 0

                        val locationText = when(location) {
                            WorkoutLocation.HOME -> "W domu"
                            WorkoutLocation.GYM -> "W silownie"
                            null -> ""
                        }

                        val trainingText = if (frequency > 0) "$frequency raz w tygodniu ($locationText)" else "-"

                        // Вывод строк
                        InfoRow(label = "Twój cel:", value = goalText)
                        InfoRow(label = "Kalorie:", value = "$calories kcal")
                        InfoRow(label = "BTW:", value = "B:$p / T:$f / W:$c (g)")
                        InfoRow(label = "Ćwiczycienia:", value = trainingText)
                    }
                }
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
                        text = "Pokaż mój plan",
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
            fontSize = 17.sp,
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
