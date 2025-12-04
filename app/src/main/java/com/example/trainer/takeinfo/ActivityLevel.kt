package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground
import com.example.trainer.ui.theme.NavigationButtons
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun ActivityLevel(
    viewModel: OnboardingViewModel? = null, // <-- Раскомментируй, когда подключишь
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // --- Блок 1: Объективные данные ---
    var workActivity by remember { mutableStateOf<String?>(null) }
    var sportsFrequency by remember { mutableStateOf<String?>(null) }
    var sessionDuration by remember { mutableStateOf<String?>(null) }

    // --- Блок 2: Субъективные ощущения ---
    var physicalCondition by remember { mutableStateOf<String?>(null) }
    var fatigueLevel by remember { mutableStateOf<String?>(null) }

    // Кнопка активна ТОЛЬКО если ответили на ВСЕ 5 вопросов
    val allAnswered = workActivity != null &&
            sportsFrequency != null &&
            sessionDuration != null &&
            physicalCondition != null &&
            fatigueLevel != null

    GradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Включаем скролл
                    .padding(horizontal = 24.dp)
                    // Важно: Большой отступ снизу, чтобы кнопки не перекрывали последний вопрос
                    .padding(bottom = 120.dp)
            ) {
                Text(
                    text = "Ваша активность",
                    modifier = Modifier.padding(top = 40.dp, bottom = 16.dp),
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "Ответьте на все вопросы, чтобы мы точно рассчитали калории.",
                    modifier = Modifier.padding(bottom = 32.dp),
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )

                // --- БЛОК 1: ОБРАЗ ЖИЗНИ ---

                ActivityQuestionSection(
                    question = "1. Ваша работа или основная дневная активность?",
                    options = listOf(
                        "Сидячая, мало движения",              // 1
                        "Работа \"на ногах\", но без нагрузок", // 2
                        "Физическая работа, много движения"    // 3
                    ),
                    selectedOption = workActivity,
                    onOptionSelected = { workActivity = it }
                )

                ActivityQuestionSection(
                    question = "2. Как часто вы занимаетесь спортом?",
                    options = listOf(
                        "Редко или никогда",      // 1
                        "Несколько раз в неделю", // 2
                        "Почти каждый день"       // 3
                    ),
                    selectedOption = sportsFrequency,
                    onOptionSelected = { sportsFrequency = it }
                )

                ActivityQuestionSection(
                    question = "3. Сколько обычно длится ваша активность?",
                    options = listOf(
                        "Менее 30 минут", // 1
                        "30 - 60 минут",  // 2
                        "Более часа"      // 3
                    ),
                    selectedOption = sessionDuration,
                    onOptionSelected = { sessionDuration = it }
                )

                // Разделитель визуальный
                Text(
                    text = "Самочувствие",
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // --- БЛОК 2: ОЩУЩЕНИЯ ---

                ActivityQuestionSection(
                    question = "4. Ваша физическая форма сейчас?",
                    options = listOf(
                        "Слабая (быстро устаю)",      // 1
                        "Средняя (могу пробежаться)", // 2
                        "Отличная (я вынослив)"       // 3
                    ),
                    selectedOption = physicalCondition,
                    onOptionSelected = { physicalCondition = it }
                )

                ActivityQuestionSection(
                    question = "5. Утомляемость в конце дня?",
                    options = listOf(
                        "Часто (валюсь с ног)", // 1
                        "Иногда",               // 2
                        "Редко (полон сил)"     // 3
                    ),
                    selectedOption = fatigueLevel,
                    onOptionSelected = { fatigueLevel = it }
                )
            }

            // Кнопки закреплены внизу
            NavigationButtons(
                onBackClick = onBackClick,
                onNextClick = {
                    // 1. Превращаем текст в баллы для ActivityAnalyzer
                    val jobScore = when(workActivity) {
                        "Сидячая, мало движения" -> 1
                        "Работа \"на ногах\", но без нагрузок" -> 2
                        else -> 3
                    }
                    val freqScore = when(sportsFrequency) {
                        "Редко или никогда" -> 1
                        "Несколько раз в неделю" -> 2
                        else -> 3
                    }
                    val durScore = when(sessionDuration) {
                        "Менее 30 минут" -> 1
                        "30 - 60 минут" -> 2
                        else -> 3
                    }
                    val condScore = when(physicalCondition) {
                        "Слабая (быстро устаю)" -> 1
                        "Средняя (могу пробежаться)" -> 2
                        else -> 3
                    }
                    val fatScore = when(fatigueLevel) {
                        "Часто (валюсь с ног)" -> 1
                        "Иногда" -> 2
                        else -> 3
                    }

                    // 2. TODO: Сохраняем всё в ViewModel

                    viewModel?.saveActivityData(
                        job = jobScore,
                        freq = freqScore,
                        dur = durScore,
                        cond = condScore,
                        fatigue = fatScore
                    )
                    onNextClick()
                },
                nextEnabled = allAnswered,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// Твой компонент вопроса (оставляем без изменений)
@Composable
fun ActivityQuestionSection(
    question: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = question,
            modifier = Modifier.padding(bottom = 12.dp),
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp
        )

        options.forEach { option ->
            val isSelected = selectedOption == option
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(56.dp)
                    .background(
                        color = if (isSelected) Color(0xFF2196F3) else Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF2196F3) else Color.Gray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onOptionSelected(option) }
            ) {
                Text(
                    text = option,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityLevelPreview() {
    TrainerTheme {
        ActivityLevel(onNextClick = {}, onBackClick = {})
    }
}