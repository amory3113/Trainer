package com.example.trainer.GeneralScreen.Stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.data.NutritionEntity
import com.example.trainer.ui.theme.GradientBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Waga i BMI", "Historia żywienia")

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Mój postęp",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF2196F3),
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF2196F3)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Medium) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> WeightTabContent(viewModel)
                1 -> NutritionTabContent(viewModel)
            }
        }
    }
}
@Composable
fun WeightTabContent(viewModel: StatsViewModel) {
    val user by viewModel.userProfile.collectAsState()
    val weightHistory by viewModel.weightHistory.collectAsState()
    val bmi = viewModel.calculateBMI()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (user != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Aktualna waga", color = Color.Gray)
                        Text(
                            text = "${user!!.weight} кг",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Wskaźnik masy ciała (BMI)", fontWeight = FontWeight.Bold)
                        Text(
                            text = String.format("%.1f", bmi),
                            fontSize = 24.sp
                        )
                        Text(
                            text = viewModel.getBmiStatus(bmi),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        BmiGauge(bmi = bmi)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Historia pomiarów", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (weightHistory.isEmpty()) {
                    Text("Brak danych.", color = Color.Gray)
                } else {
                    LineChart(dataPoints = weightHistory)
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF2196F3)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodać", tint = Color.White)
        }
    }

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
fun NutritionTabContent(viewModel: StatsViewModel) {
    val nutritionList by viewModel.nutritionHistory.collectAsState()

    if (nutritionList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Historia żywności jest pusta", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(nutritionList) { item ->
                NutritionHistoryItem(item)
            }
        }
    }
}

@Composable
fun NutritionHistoryItem(item: NutritionEntity) {
    val dateFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Дата
            Text(
                text = dateFormat.format(Date(item.date)),
                color = Color.Gray,
                fontSize = 14.sp
            )

            // Калории и БЖУ
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.calories} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = "B:${item.protein}  T:${item.fat}  W:${item.carbs}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AddWeightDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nowe ważenie") },
        text = {
            Column {
                Text("Wprowadź swoją aktualną wagę:")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text("Kg") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val weight = text.toFloatOrNull()
                if (weight != null) onConfirm(weight)
            }) {
                Text("Zapisać")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anulować")
            }
        }
    )
}