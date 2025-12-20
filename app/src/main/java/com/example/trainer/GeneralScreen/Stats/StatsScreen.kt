package com.example.trainer.GeneralScreen.Stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val weightHistory by viewModel.weightHistory.collectAsState() // Читаем историю
    val bmi = viewModel.calculateBMI()

    var showDialog by remember { mutableStateOf(false) }

    GradientBackground {
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
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

                // Если истории нет - покажи заглушку
                if (weightHistory.isEmpty()) {
                    Text("Нет данных. Взвесьтесь!", color = Color.Gray)
                } else {
                    LineChart(dataPoints = weightHistory)
                }
            }

                // ПЛАВАЮЩАЯ КНОПКА (Снизу справа)
                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить вес", tint = Color.White)
                }
            }
        }
    }

    // САМ ДИАЛОГ
    if (showDialog) {
        AddWeightDialog(
            onDismiss = { showDialog = false },
            onConfirm = { newWeight ->
                viewModel.addNewWeight(newWeight)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddWeightDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новое взвешивание") },
        text = {
            Column {
                Text("Введите ваш текущий вес:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text("Кг") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val weight = text.toFloatOrNull()
                if (weight != null) {
                    onConfirm(weight)
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}