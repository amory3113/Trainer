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
    viewModel: OnboardingViewModel? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var workActivity by remember { mutableStateOf<String?>(null) }
    var sportsFrequency by remember { mutableStateOf<String?>(null) }
    var sessionDuration by remember { mutableStateOf<String?>(null) }

    var physicalCondition by remember { mutableStateOf<String?>(null) }
    var fatigueLevel by remember { mutableStateOf<String?>(null) }

    val allAnswered = workActivity != null &&
            sportsFrequency != null &&
            sessionDuration != null &&
            physicalCondition != null &&
            fatigueLevel != null

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Twoja aktywność",
                    modifier = Modifier.padding(top = 40.dp, bottom = 16.dp),
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "Proszę odpowiedzieć na wszystkie pytania, abyśmy mogli dokładnie obliczyć Twoje spożycie kalorii.",
                    modifier = Modifier.padding(bottom = 32.dp),
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )

                ActivityQuestionSection(
                    question = "1. Jaka jest Twoja praca lub główne codzienne zajęcie?",
                    options = listOf(
                        "Siedzący tryb życia, mało ruchu",
                        "Pracuj \"na stojąco\", ale bez stresu",
                        "Praca fizyczna, dużo ruchu"
                    ),
                    selectedOption = workActivity,
                    onOptionSelected = { workActivity = it }
                )

                ActivityQuestionSection(
                    question = "2. Jak często ćwiczysz?",
                    options = listOf(
                        "Rzadko lub nigdy",
                        "Kilka razy w tygodniu",
                        "Prawie każdego dnia"
                    ),
                    selectedOption = sportsFrequency,
                    onOptionSelected = { sportsFrequency = it }
                )

                ActivityQuestionSection(
                    question = "3. Jak długo zazwyczaj trwa Twoja aktywność?",
                    options = listOf(
                        "Mniej niż 30 minut",
                        "30 - 60 minut",
                        "Ponad godzinę"
                    ),
                    selectedOption = sessionDuration,
                    onOptionSelected = { sessionDuration = it }
                )

                Text(
                    text = "Samopoczucie",
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                ActivityQuestionSection(
                    question = "4. Jaka jest Twoja aktualna kondycja fizyczna?",
                    options = listOf(
                        "Słaby (szybko się męczy)",
                        "Średni (potrafię biegać)",
                        "Świetnie (jestem odporny)"
                    ),
                    selectedOption = physicalCondition,
                    onOptionSelected = { physicalCondition = it }
                )

                ActivityQuestionSection(
                    question = "5. Zmęczenie pod koniec dnia?",
                    options = listOf(
                        "Często (spadam z nóg)",
                        "Czasami",
                        "Rzadko (pełen energii)"
                    ),
                    selectedOption = fatigueLevel,
                    onOptionSelected = { fatigueLevel = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            NavigationButtons(
                onBackClick = onBackClick,
                onNextClick = {
                    val jobScore = when(workActivity) {
                        "Siedzący tryb życia, mało ruchu" -> 1
                        "Pracuj \"na stojąco\", ale bez stresu" -> 2
                        else -> 3
                    }
                    val freqScore = when(sportsFrequency) {
                        "Rzadko lub nigdy" -> 1
                        "Kilka razy w tygodniu" -> 2
                        else -> 3
                    }
                    val durScore = when(sessionDuration) {
                        "Mniej niż 30 minut" -> 1
                        "30 - 60 minut" -> 2
                        else -> 3
                    }
                    val condScore = when(physicalCondition) {
                        "Słaby (szybko się męczy)" -> 1
                        "Średni (potrafię biegać)" -> 2
                        else -> 3
                    }
                    val fatScore = when(fatigueLevel) {
                        "Często (spadam z nóg)" -> 1
                        "Czasami" -> 2
                        else -> 3
                    }

                    viewModel?.calculateActivityLevel(
                        job = jobScore,
                        sport = freqScore,
                        duration = durScore,
                        condition = condScore,
                        fatigue = fatScore
                    )
                    onNextClick()
                },
                nextEnabled = allAnswered
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

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