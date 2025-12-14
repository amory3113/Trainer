package com.example.trainer.GeneralScreen.Stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val bmi = viewModel.calculateBMI()

    GradientBackground {
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Мой прогресс",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // 1. Карточка ТЕКУЩИЙ ВЕС
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Текущий вес", color = Color.Gray, fontSize = 14.sp)
                            Text(
                                text = "${user!!.weight} кг",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                        // Сюда можно добавить мини-индикатор изменений (например "-2кг")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Карточка ИМТ (BMI)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("Индекс массы (ИМТ)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(String.format("%.1f", bmi), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = viewModel.getBmiStatus(bmi),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Наша цветная полоска
                        BmiGauge(bmi = bmi)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. График веса
                Text(
                    text = "История веса",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Сам график
                LineChart(dataPoints = viewModel.weightHistory)
            }
        }
    }
}