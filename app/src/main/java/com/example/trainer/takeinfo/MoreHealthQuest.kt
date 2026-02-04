package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.LightBlue
import com.example.trainer.ui.theme.NavigationButtons
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun MoreHealthQuest(
    viewModel: OnboardingViewModel? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    var question1 by remember { mutableStateOf<String?>(null) }
    var question2 by remember { mutableStateOf<String?>(null) }
    var question3 by remember { mutableStateOf<String?>(null) }
    var question4 by remember { mutableStateOf<String?>(null) }
    var question5 by remember { mutableStateOf<String?>(null) }

    val allAnswered = question1 != null && question2 != null && question3 != null && question4 != null && question5 != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LightBlue,
                        Color.White
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) {
            Text(
                text = "Wyjaśnijmy Twój stan",
                modifier = Modifier.padding(top = 40.dp, bottom = 16.dp),
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                lineHeight = 32.sp
            )

            Text(
                text = "Oceń poniższe punkty, abyśmy mogli wybrać bezpieczne ćwiczenia.",
                modifier = Modifier.padding(bottom = 32.dp),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )

            QuestionSection(
                question = "Choroby przewlekłe (cukrzyca, nadciśnienie)?",
                options = listOf("Nie", "Nieznaczne", "Poważne"),
                selectedOption = question1,
                onOptionSelected = { question1 = it }
            )

            QuestionSection(
                question = "Bóle stawów/pleców lub niedawne urazy?",
                options = listOf("Nie", "Czasami", "Poważne lub niedawne"),
                selectedOption = question2,
                onOptionSelected = { question2 = it }
            )

            QuestionSection(
                question = "Choroby serca i układu krążenia?",
                options = listOf("Nie", "Łagodne", "Poważne"),
                selectedOption = question3,
                onOptionSelected = { question3 = it }
            )

            QuestionSection(
                question = "Czy lekarz zaleca unikanie pewnych czynności?",
                options = listOf("Nie", "Częściowo", "Całkowicie"),
                selectedOption = question4,
                onOptionSelected = { question4 = it }
            )

            QuestionSection(
                question = "Jak często odczuwasz zmęczenie podczas codziennych czynności?",
                options = listOf("Rzadko", "Często", "Ciągle"),
                selectedOption = question5,
                onOptionSelected = { question5 = it }
            )
        }

        NavigationButtons(
            onBackClick = onBackClick,
            onNextClick = {
                fun parse(answer: String?): Int {
                    return when (answer) {
                        "Nie", "Rzadko" -> 0
                        "Niewielkie", "Czasami", "Łagodne", "Częściowe", "Często" -> 1
                        else -> 3
                    }
                }

                viewModel?.calculateHealthRisks(
                    chronic = parse(question1),
                    injuries = parse(question2),
                    heart = parse(question3),
                    restrictions = parse(question4),
                    fatigue = parse(question5)
                )
                onNextClick()
            },
            nextEnabled = allAnswered,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun QuestionSection(
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp
        )

        options.forEach { option ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(50.dp)
                    .background(
                        color = if (selectedOption == option) Color(0xFF2196F3) else Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onOptionSelected(option) }
            ) {
                Text(
                    text = option,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (selectedOption == option) Color.White else Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoreHealthQuestPreview() {
    TrainerTheme {
        MoreHealthQuest(onNextClick = {})
    }
}