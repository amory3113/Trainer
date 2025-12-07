package com.example.trainer.GeneralScreen.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground
import com.example.trainer.ui.theme.LightBlue

@Composable
fun Profile(
    viewModel: ProfileViewModel? = null, // ViewModel теперь нужен
    onLogout: () -> Unit = {} // Действие при выходе
) {
    // Читаем данные из ViewModel
    val userProfile by viewModel?.userProfile?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(null) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Профиль",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 30.dp)
            )

            // --- АВАТАРКА (Заглушка) ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- КАРТОЧКА С ДАННЫМИ ---
            if (userProfile != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        ProfileRow(label = "Пол", value = userProfile!!.gender)
                        ProfileRow(label = "Возраст", value = "${userProfile!!.age} лет")
                        ProfileRow(label = "Вес", value = "${userProfile!!.weight} кг")
                        ProfileRow(label = "Рост", value = "${userProfile!!.height} см")
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        ProfileRow(label = "Цель", value = userProfile!!.goal)
                        ProfileRow(label = "Активность", value = userProfile!!.activityLevel)
                    }
                }
            } else {
                // Если данных нет (загрузка или ошибка)
                CircularProgressIndicator(color = Color(0xFF2196F3))
            }

            Spacer(modifier = Modifier.weight(1f)) // Толкаем кнопку вниз

            // --- КНОПКА СБРОСА ---
            Button(
                onClick = {
                    viewModel?.clearData {
                        onLogout() // Уходим на экран приветствия
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сбросить все данные", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(80.dp)) // Отступ под нижнее меню
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp)
        Text(text = value, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}