package com.example.trainer.GeneralScreen.Home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.trainer.R
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

    var showWorkoutDialog by remember { mutableStateOf(false) }
    var selectedImageForZoom by remember { mutableStateOf<String?>(null) }

    GradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = getGreeting(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = getCurrentDate(),
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .aspectRatio(1f)
                    ) {
                        CircularProgressbar(
                            modifier = Modifier.fillMaxSize(),
                            calories = uiState.caloriesEaten,
                            remainingCalories = viewModel.getCaloriesRemaining(),
                            progress = viewModel.getCaloriesProgress()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    NutrientBar(
                        name = "Białko",
                        current = uiState.proteinEaten,
                        target = uiState.userProfile?.proteinGrams ?: 0,
                        progress = viewModel.getProteinProgress(),
                        color = Color(0xFF4CAF50)
                    )
                    NutrientBar(
                        name = "Tłuszcze",
                        current = uiState.fatEaten,
                        target = uiState.userProfile?.fatGrams ?: 0,
                        progress = viewModel.getFatProgress(),
                        color = Color(0xFFFFC107)
                    )
                    NutrientBar(
                        name = "Węglowodany",
                        current = uiState.carbsEaten,
                        target = uiState.userProfile?.carbGrams ?: 0,
                        progress = viewModel.getCarbsProgress(),
                        color = Color(0xFF9C27B0)
                    )
                }

                if (uiState.todayWorkoutName != null) {
                    WorkoutCard(
                        workoutName = uiState.todayWorkoutName!!,
                        onClick = { showWorkoutDialog = true }
                    )
                } else {
                    RestDayCard()
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            ExtendedFloatingActionButton(
                onClick = { showFoodDialog = true },
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Dodaj jedzenie", fontWeight = FontWeight.Bold)
            }
        }
    }


    if (showFoodDialog) {
        AddFoodDialog(
            onDismiss = { },
            onConfirm = { k, p, f, c ->
                viewModel.addFood(k, p, f, c)
            }
        )
    }

    if (showWorkoutDialog && uiState.todayWorkoutName != null) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = uiState.todayWorkoutName ?: "Trening",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    uiState.todayExercises.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val context = LocalContext.current
                            val imageRes = getDrawableIdByName(context, item.exercise.imageName)

                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable { selectedImageForZoom = item.exercise.imageName },
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = item.exercise.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${item.sets} serie x ${item.reps} powt.",
                                    color = Color(0xFF2196F3),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { }) {
                    Text("Zamknij")
                }
            }
        )
    }

    if (selectedImageForZoom != null) {
        Dialog(onDismissRequest = { }) {
            val context = LocalContext.current
            val imageRes = getDrawableIdByName(context, selectedImageForZoom)

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().background(Color.White)
                    )
                    IconButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(workoutName: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                Text(text = "Dzisiaj", fontSize = 14.sp, color = Color.Gray)
                Text(text = workoutName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2196F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Details",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun RestDayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
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
    val sdf = SimpleDateFormat("EEEE, d MMMM", Locale("pl", "PL"))
    return sdf.format(Date()).replaceFirstChar { it.uppercase() }
}

@Composable
fun getDrawableIdByName(context: Context, name: String?): Int {
    if (name.isNullOrEmpty()) return R.drawable.ic_launcher_foreground
    val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
    return if (resId != 0) resId else R.drawable.ic_launcher_foreground
}