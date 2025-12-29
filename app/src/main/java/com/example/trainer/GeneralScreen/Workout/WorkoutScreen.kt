package com.example.trainer.GeneralScreen.Workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainer.data.AppDatabase
import com.example.trainer.data.Exercise.ScheduleEntity
import com.example.trainer.data.Exercise.WorkoutRepository
import com.example.trainer.data.Exercise.WorkoutTemplateEntity
import com.example.trainer.data.Exercise.WorkoutWithExercises
import com.example.trainer.ui.theme.GradientBackground

@Composable
fun WorkoutScreen(onNavigateToCreate: () -> Unit) {
    // 1. Получаем доступ к базе
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = remember { WorkoutRepository(database.workoutDao()) }

    // ИСПРАВЛЕНИЕ 1: Мы добавили ExerciseDao в фабрику (так как мы обновили ViewModel ранее)
    val viewModel: WorkoutViewModel = viewModel(
        factory = WorkoutViewModelFactory(repository, database.exerciseDao())
    )

    // Состояние вкладок
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Мои программы", "Расписание")

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ВЕРХНИЕ ВКЛАДКИ
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF2196F3),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF2196F3)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // КОНТЕНТ
            when (selectedTab) {
                // ИСПРАВЛЕНИЕ 2: Передаем функцию навигации внутрь вкладки
                0 -> MyProgramsTab(viewModel, onNavigateToCreate)
                1 -> ScheduleTab(viewModel)
            }
        }
    }
}

// --- ВКЛАДКА 1: ПРОГРАММЫ ---
@Composable
fun MyProgramsTab(
    viewModel: WorkoutViewModel,
    onNavigateToCreate: () -> Unit // ИСПРАВЛЕНИЕ 3: Принимаем этот параметр здесь
) {
    val templates by viewModel.templates.collectAsState()

    // Состояния для диалога подтверждения
    var showDeleteDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<WorkoutTemplateEntity?>(null) }

    if (showDeleteDialog && workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить тренировку?") },
            text = { Text("Вы уверены, что хотите удалить \"${workoutToDelete?.name}\"? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        workoutToDelete?.let { viewModel.deleteWorkout(it.id) }
                        showDeleteDialog = false
                        workoutToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (templates.isEmpty()) {
            Text(
                text = "У вас пока нет программ.\nНажмите +, чтобы создать.",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templates) { item ->
                    WorkoutCard(item = item,
                        onDeleteClick = {
                            workoutToDelete = item.template // Запоминаем, что хотим удалить
                            showDeleteDialog = true         // Показываем диалог
                        }
                    )
                }
            }
        }

        // Кнопка создания (+)
        FloatingActionButton(
            onClick = { onNavigateToCreate() }, // Теперь эта переменная видна!
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 80.dp), // Отступ от нижнего меню
            containerColor = Color(0xFF2196F3)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Создать", tint = Color.White)
        }
    }
}

@Composable
fun WorkoutCard(item: WorkoutWithExercises, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column ( modifier = Modifier.weight(1f))
            {
                // Название программы
                Text(item.template.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(4.dp))

                // СПИСОК УПРАЖНЕНИЙ (Вместо описания)
                if (item.exercises.isNotEmpty()) {
                    // Берем имена, соединяем через запятую
                    val exercisesList = item.exercises.joinToString(separator = ", ") { it.name }
                    Text(
                        text = exercisesList,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                } else {
                    Text("Нет упражнений", fontSize = 14.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.LightGray // Делаем серым, чтобы не бросалось в глаза
                )
            }
        }
    }
}

// --- ИЗМЕНЕННАЯ ВКЛАДКА 2: РАСПИСАНИЕ ---
@Composable
fun ScheduleTab(viewModel: WorkoutViewModel) {
    val schedule by viewModel.schedule.collectAsState()
    val allTemplates by viewModel.templates.collectAsState() // Список всех программ для выбора

    // Состояние диалога выбора
    var showDialog by remember { mutableStateOf(false) }
    var selectedDayIndex by remember { mutableIntStateOf(-1) }

    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

    // Диалог выбора тренировки
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Выберите тренировку на ${daysOfWeek[selectedDayIndex]}") },
            text = {
                LazyColumn {
                    item {
                        // Опция "Выходной"
                        Text(
                            "Сделать выходным",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.clearDay(selectedDayIndex)
                                    showDialog = false
                                }
                                .padding(12.dp),
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        HorizontalDivider()
                    }
                    items(allTemplates) { item ->
                        Text(
                            item.template.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.assignWorkoutToDay(selectedDayIndex, item)
                                    showDialog = false
                                }
                                .padding(12.dp),
                            fontSize = 18.sp
                        )
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Отмена") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daysOfWeek.indices.toList()) { index ->
            val dayName = daysOfWeek[index]
            val scheduleItem = schedule.find { it.dayOfWeek == index }

            ScheduleDayCard(
                dayName = dayName,
                item = scheduleItem,
                onClick = {
                    selectedDayIndex = index
                    showDialog = true
                }
            )
        }
    }
}

@Composable
fun ScheduleDayCard(
    dayName: String,
    item: ScheduleEntity?,
    onClick: () -> Unit // Добавили клик
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Карточка теперь кликабельна
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(dayName, fontWeight = FontWeight.Medium, fontSize = 16.sp)

            if (item != null && item.workoutName != null) {
                // Если тренировка есть - показываем название синим
                Text(
                    text = item.workoutName,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold
                )
            } else {
                // Если тренировки нет - пишем "Выходной / Назначить" серым
                Text("Выходной / Назначить", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}