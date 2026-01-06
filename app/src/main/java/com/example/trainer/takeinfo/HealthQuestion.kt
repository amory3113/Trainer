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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun HealthQuestion(
    viewModel: OnboardingViewModel? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNoLimitations: () -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

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
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Ważne pytanie o zdrowie",
                modifier = Modifier.padding(top = 40.dp, bottom = 24.dp),
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                lineHeight = 32.sp
            )

            Text(
                text = "Czy wiesz o jakichś poważnych ograniczeniach, urazach lub przewlekłych chorobach?",
                modifier = Modifier.padding(bottom = 40.dp),
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(60.dp)
                    .background(
                        color = if (selectedOption == "no") Color(0xFF2196F3) else Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedOption = "no" }
            ) {
                Text(
                    text = "Nie, nie mam żadnych ograniczeń.",
                    modifier = Modifier.align(Alignment.Center),
                    color = if (selectedOption == "no") Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = if (selectedOption == "yes") Color(0xFF2196F3) else Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedOption = "yes" }
            ) {
                Text(
                    text = "Tak, mam ograniczenia.",
                    modifier = Modifier.align(Alignment.Center),
                    color = if (selectedOption == "yes") Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 26.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onBackClick() },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = "Z powrotem",
                    color = Color(0xFF2196F3),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    when (selectedOption) {
                        "no" -> {
                            viewModel?.setHealthyStatus()
                            onNoLimitations()
                        }
                        "yes" -> {
                            onNextClick()
                        }
                    }
                },
                enabled = selectedOption != null,
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Następny",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthQuestionPreview() {
    TrainerTheme {
        HealthQuestion(onNextClick = {}, onNoLimitations = {})
    }
}