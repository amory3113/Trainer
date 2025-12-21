package com.example.trainer.GeneralScreen.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground
import com.example.trainer.data.UserEntity

@Composable
fun Profile(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(null) }
    val scrollState = rememberScrollState()

    // --- НОВОЕ: Состояния для управления диалогом ---
    var showDialog by remember { mutableStateOf(false) }
    var editType by remember { mutableStateOf(EditType.WEIGHT) } // По умолчанию Вес
    var editValue by remember { mutableStateOf("") }

    GradientBackground {
        if (userProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. ШАПКА ПРОФИЛЯ
                ProfileHeader(userProfile!!)

                Spacer(modifier = Modifier.height(24.dp))

                // 2. БЛОК "МОЕ ТЕЛО"
                SectionTitle("Мои данные")
                CardSection {
                    ProfileOptionItem(
                        icon = Icons.Default.AccessibilityNew,
                        title = "Параметры тела",
                        value = "${userProfile!!.height} см / ${userProfile!!.weight} кг",
                        onClick = { editType = EditType.WEIGHT
                            editValue = userProfile!!.weight.toString()
                            showDialog = true
                        }
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileOptionItem(
                        icon = Icons.Default.Cake,
                        title = "Возраст",
                        value = "${userProfile!!.age} лет",
                        onClick = { /* Возраст обычно не меняют часто, можно оставить пустым или добавить позже */ }
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileOptionItem(
                        icon = Icons.Default.Transgender,
                        title = "Пол",
                        value = formatGender(userProfile!!.gender),
                        showArrow = false // Пол обычно менять нельзя
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. БЛОК "ПРОГРАММА"
                SectionTitle("Настройки программы")
                CardSection {
                    ProfileOptionItem(
                        icon = Icons.Default.Flag,
                        title = "Цель",
                        value = formatGoal(userProfile!!.goal),
                        onClick = { editType = EditType.GOAL
                            editValue = userProfile!!.goal
                            showDialog = true
                        }
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileOptionItem(
                        icon = Icons.Default.FitnessCenter,
                        title = "Активность",
                        value = formatActivity(userProfile!!.activityLevel),
                        onClick = { editType = EditType.ACTIVITY
                            editValue = userProfile!!.activityLevel
                            showDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. БЛОК "ПРИЛОЖЕНИЕ"
                SectionTitle("Приложение")
                CardSection {
                    ProfileOptionItem(
                        icon = Icons.Default.Info,
                        title = "О приложении",
                        value = "Версия 1.0 (Диплом)",
                        showArrow = false
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. КНОПКА СБРОСА (ВЫХОДА)
                OutlinedButton(
                    onClick = {
                        viewModel.clearData { onLogout() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Outlined.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сбросить прогресс и выйти")
                }

                Spacer(modifier = Modifier.height(100.dp)) // Отступ для нижнего меню
            }
        }
    }
    // --- НОВОЕ: ВЫЗОВ ДИАЛОГА ---
    if (showDialog) {
        EditProfileDialog(
            type = editType,
            currentValue = editValue,
            onDismiss = { showDialog = false },
            onConfirm = { newValue ->
                viewModel.updateProfile(editType, newValue) // Отправляем во ViewModel
                showDialog = false
            }
        )
    }
}

// --- КОМПОНЕНТЫ (Можно вынести в отдельные файлы, но для удобства оставил тут) ---

@Composable
fun ProfileHeader(user: UserEntity) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(32.dp))

        // Аватарка с обводкой
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .border(2.dp, Color(0xFF2196F3), CircleShape)
                .padding(4.dp) // Отступ между обводкой и фото
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Пользователь", // Можно добавить поле Имя в базу, но пока так
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Ваш ID: ${user.id}",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun CardSection(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    value: String,
    showArrow: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Иконка слева
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Заголовок
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Значение (серое)
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            // Стрелочка
            if (showArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- Вспомогательные функции форматирования ---
fun formatGender(gender: String): String {
    return when(gender) {
        "MALE" -> "Мужской"
        "FEMALE" -> "Женский"
        else -> gender
    }
}

fun formatGoal(goal: String): String {
    return when(goal) {
        "WEIGHT_LOSS" -> "Похудение"
        "MUSCLE_GAIN" -> "Набор массы"
        "MAINTAIN_FITNESS" -> "Поддержка"
        else -> goal
    }
}

fun formatActivity(activity: String): String {
    return when(activity) {
        "BEGINNER" -> "Низкая"
        "INTERMEDIATE" -> "Средняя"
        "ADVANCED" -> "Высокая"
        else -> activity
    }
}