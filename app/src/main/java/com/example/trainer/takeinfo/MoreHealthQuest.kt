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
    onBackClick: () -> Unit
) {
    var heartAnswer by remember { mutableStateOf<String?>(null) }
    var jointAnswer by remember { mutableStateOf<String?>(null) }

    val allAnswered = heartAnswer != null && jointAnswer != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LightBlue, Color.White),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Twój stan zdrowia",
                modifier = Modifier.padding(top = 40.dp, bottom = 16.dp),
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                lineHeight = 32.sp
            )

            Text(
                text = "Te informacje są kluczowe, aby wykluczyć niebezpieczne ćwiczenia.",
                modifier = Modifier.padding(bottom = 32.dp),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )

            QuestionSection(
                question = "1. Czy masz problemy z sercem lub nadciśnieniem?",
                options = listOf("Nie", "Tak"),
                selectedOption = heartAnswer,
                onOptionSelected = { heartAnswer = it }
            )

            QuestionSection(
                question = "2. Czy masz problemy z kręgosłupem lub stawami?",
                options = listOf("Nie", "Tak"),
                selectedOption = jointAnswer,
                onOptionSelected = { jointAnswer = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        NavigationButtons(
            onBackClick = onBackClick,
            onNextClick = {
                val hasHeartIssues = heartAnswer == "Tak"
                val hasJointIssues = jointAnswer == "Tak"

                viewModel?.setHealthConditions(hasHeartIssues, hasJointIssues)
                onNextClick()
            },
            nextEnabled = allAnswered,
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
                    .height(50.dp)
                    .background(
                        color = if (isSelected) Color(0xFF2196F3) else Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF2196F3) else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onOptionSelected(option) }
            ) {
                Text(
                    text = option,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (isSelected) Color.White else Color.Black,
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
        MoreHealthQuest(onNextClick = {}, onBackClick = {})
    }
}