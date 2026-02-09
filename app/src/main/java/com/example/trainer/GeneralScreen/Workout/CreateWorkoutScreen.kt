package com.example.trainer.GeneralScreen.Workout

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.R
import com.example.trainer.ui.theme.ButtonBlue
import com.example.trainer.ui.theme.GradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    viewModel: WorkoutViewModel,
    onBack: () -> Unit
) {
    val workoutName by viewModel.workoutName.collectAsState()
    var showExerciseSelector by remember { mutableStateOf(false) }
    val selectedExercises by viewModel.selectedExercises.collectAsState()

    var exerciseToEdit by remember { mutableStateOf<WorkoutExerciseUiState?>(null) }

    if (showExerciseSelector) {
        ExerciseSelectorScreen(
            viewModel = viewModel,
            onClose = { }
        )
    } else {
        GradientBackground {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text(if (workoutName.isEmpty()) "Nowy trening" else "Edycja treningu") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            viewModel.saveWorkout()
                            onBack()
                        },
                        containerColor = Color(0xFF2196F3)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить", tint = Color.White)
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Nazwa treningu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonBlue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = ButtonBlue,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ćwiczenia (${selectedExercises.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Add, contentDescription = "Добавить", tint = Color(0xFF2196F3))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedExercises) { item ->
                            ExerciseItem(
                                item = item,
                                onDelete = { viewModel.removeExercise(item.exercise.id) },
                                onEdit = { exerciseToEdit = item }
                            )
                        }
                    }
                }
            }
        }
    }

    if (exerciseToEdit != null) {
        EditSetsRepsDialog(
            item = exerciseToEdit!!,
            onDismiss = { },
            onConfirm = { sets, reps ->
                viewModel.updateExerciseDetails(exerciseToEdit!!.exercise.id, sets, reps)
            }
        )
    }
}

@Composable
fun ExerciseItem(
    item: WorkoutExerciseUiState,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            val imageRes = getDrawableIdByName(context, item.exercise.imageName)

            androidx.compose.foundation.Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(item.exercise.name, fontWeight = FontWeight.Medium)
                Text(
                    text = "${item.sets} serie x ${item.reps} powt.",
                    fontSize = 14.sp,
                    color = Color(0xFF2196F3)
                )
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun EditSetsRepsDialog(
    item: WorkoutExerciseUiState,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var sets by remember { mutableStateOf(item.sets.toString()) }
    var reps by remember { mutableStateOf(item.reps.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = item.exercise.name) },
        text = {
            Column {
                Text("Dostosuj trening:")
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it },
                        label = { Text("Serie") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Powt.") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val s = sets.toIntOrNull() ?: 3
                val r = reps.toIntOrNull() ?: 12
                onConfirm(s, r)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}

@Composable
fun ExerciseSelectorScreen(
    viewModel: WorkoutViewModel,
    onClose: () -> Unit
) {
    val availableExercises by viewModel.availableExercises.collectAsState()
    val categories = availableExercises.map { it.muscleGroup }.distinct()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Wybierz ćwiczenia", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Wszystkie") }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(translateCategory(cat)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val listToShow = if (selectedCategory == null) availableExercises else availableExercises.filter { it.muscleGroup == selectedCategory }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(listToShow) { exercise ->
                    val isSelected = selectedIds.contains(exercise.id)
                    val cardColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
                    val borderColor = if (isSelected) Color(0xFF2196F3) else Color.Transparent

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedIds = if (isSelected) {
                                    selectedIds - exercise.id
                                } else {
                                    selectedIds + exercise.id
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = BorderStroke(2.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedIds = if (checked) selectedIds + exercise.id else selectedIds - exercise.id
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val context = LocalContext.current
                            val imageRes = getDrawableIdByName(context, exercise.imageName)

                            androidx.compose.foundation.Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).padding(end = 12.dp)
                            )
                            Column {
                                Text(exercise.name, fontWeight = FontWeight.Bold)
                                Text(translateCategory(exercise.muscleGroup), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anuluj")
                }

                Button(
                    onClick = {
                        val exercisesToAdd = availableExercises.filter { it.id in selectedIds }
                        exercisesToAdd.forEach { viewModel.addExercise(it) }
                        onClose()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedIds.isNotEmpty()
                ) {
                    Text(if (selectedIds.isEmpty()) "Dodaj" else "Dodaj (${selectedIds.size})")
                }
            }
        }
    }
}

fun translateCategory(cat: String): String {
    return when(cat.uppercase()) {
        "CHEST" -> "Klatka piersiowa"
        "BACK" -> "Plecy"
        "LEGS" -> "Nogi"
        "ARMS" -> "Ramiona"
        "ABS" -> "Brzuch"
        "SHOULDERS" -> "Barki"
        "UP" -> "Górna cześć"
        "DOWN" -> "Dolna cześć"
        "CARDIO" -> "Kardio"
        else -> cat
    }
}

@Composable
fun getDrawableIdByName(context: Context, name: String?): Int {
    if (name.isNullOrEmpty()) return R.drawable.ic_launcher_foreground
    val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
    return if (resId != 0) resId else R.drawable.ic_launcher_foreground
}