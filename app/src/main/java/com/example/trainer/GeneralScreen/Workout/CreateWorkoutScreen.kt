package com.example.trainer.GeneralScreen.Workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.data.Exercise.ExerciseEntity
import com.example.trainer.ui.theme.GradientBackground
import com.example.trainer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    viewModel: WorkoutViewModel,
    onBack: () -> Unit
) {
    val workoutName by viewModel.workoutName.collectAsState()
    var showExerciseSelector by remember { mutableStateOf(false) }

    val selectedExercises by viewModel.selectedExercises.collectAsState()

    // Данные для фильтрации
    val filteredExercises by viewModel.filteredExercises.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = viewModel.categories

    if (showExerciseSelector) {
        // --- ЭКРАН ВЫБОРА (МЕНЮ) ---
        SelectExercisesView(
            exercisesToShow = filteredExercises,
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelect = { viewModel.selectCategory(it) },
            selectedExercises = selectedExercises,
            onToggle = { viewModel.toggleExerciseSelection(it) },
            onDone = { showExerciseSelector = false }
        )
    } else {
        // --- ЭКРАН СОЗДАНИЯ ---
        GradientBackground {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("Новая программа") },
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.clearSelection()
                                onBack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    // 1. НАЗВАНИЕ ТРЕНИРОВКИ (Вот тут пользователь называет её)
                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Название (например: День Груди)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Список упражнений:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. СПИСОК УЖЕ ВЫБРАННЫХ (Короткий)
                    if (selectedExercises.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Список пуст. Нажмите кнопку ниже.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(selectedExercises) { exercise ->
                                SimpleExerciseItem(exercise, onDelete = { viewModel.toggleExerciseSelection(exercise) })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. КНОПКИ
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showExerciseSelector = true },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Упражнение")
                        }

                        Button(
                            onClick = { viewModel.saveWorkout { onBack() } },
                            modifier = Modifier.weight(1f).height(50.dp),
                            enabled = selectedExercises.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Сохранить") }
                    }
                }
            }
        }
    }
}

// --- НОВЫЙ ЭКРАН ВЫБОРА С КАТЕГОРИЯМИ ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectExercisesView(
    exercisesToShow: List<ExerciseEntity>,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit,
    selectedExercises: List<ExerciseEntity>,
    onToggle: (ExerciseEntity) -> Unit,
    onDone: () -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Каталог упражнений") },
                    actions = {
                        IconButton(onClick = onDone) {
                            Icon(Icons.Default.Check, contentDescription = "Готово")
                        }
                    }
                )
                // ЛЕНТА КАТЕГОРИЙ (Chips)
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = (category == "Все" && selectedCategory == null) || category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(category) },
                            label = { Text(translateCategory(category)) } // Функция перевода ниже
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (exercisesToShow.isEmpty()) {
                item {
                    Text(
                        "В этой категории пока пусто",
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            items(exercisesToShow) { exercise ->
                val isSelected = selectedExercises.contains(exercise)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(exercise) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isSelected, onCheckedChange = { onToggle(exercise) })

                    Spacer(modifier = Modifier.width(16.dp))

                    // --- ДОБАВЛЯЕМ КАРТИНКУ СЮДА ---
                    val context = androidx.compose.ui.platform.LocalContext.current
                    // Получаем ID картинки по имени из базы
                    val imageRes = getDrawableIdByName(context, exercise.imageName)

                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp) // Размер картинки
                            .padding(end = 16.dp) // Отступ справа до текста
                    )
                    // -------------------------------

                    Column {
                        Text(exercise.name, fontWeight = FontWeight.Bold)
                        Text(translateCategory(exercise.muscleGroup), fontSize = 12.sp, color = Color.Gray)
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SimpleExerciseItem(exercise: ExerciseEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            // horizontalArrangement = Arrangement.SpaceBetween, <-- УБЕРИ ЭТО (будет мешать)
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- ДОБАВЛЯЕМ КАРТИНКУ ---
            val context = androidx.compose.ui.platform.LocalContext.current
            val imageRes = getDrawableIdByName(context, exercise.imageName)

            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )
            // --------------------------

            // Название (добавь weight(1f), чтобы занять всё место до кнопки удаления)
            Text(exercise.name, modifier = Modifier.weight(1f))

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Gray)
            }
        }
    }
}

// Простой переводчик для отображения
fun translateCategory(cat: String): String {
    return when(cat.uppercase()) {
        "CHEST" -> "Грудь"
        "BACK" -> "Спина"
        "LEGS" -> "Ноги"
        "ARMS" -> "Руки"
        "ABS" -> "Пресс"
        "SHOULDERS" -> "Плечи"
        "UP" -> "Верх тела"
        "DOWN" -> "Низ тела"
        else -> cat
    }
}

// --- В САМЫЙ НИЗ ФАЙЛА, ВНЕ КЛАССОВ ---

@Composable
fun getDrawableIdByName(context: android.content.Context, name: String?): Int {
    if (name.isNullOrEmpty()) return R.drawable.ic_launcher_foreground // Заглушка

    val resId = context.resources.getIdentifier(
        name,
        "drawable",
        context.packageName
    )
    return if (resId != 0) resId else R.drawable.ic_launcher_foreground // Заглушка, если картинки нет
}