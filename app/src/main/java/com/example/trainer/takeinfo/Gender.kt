package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.Logic.Models.Gender
import com.example.trainer.ui.theme.ButtonBlue
import com.example.trainer.ui.theme.LightBlue
import com.example.trainer.ui.theme.NavigationButtons
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun GenderScreen(
    viewModel: OnboardingViewModel? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val (selectedGender, setSelectedGender) = remember { mutableStateOf<Gender?>(null) }

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
                .align(Alignment.Center)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Teraz potrzebujemy informacji o Tobie.",
                modifier = Modifier
                    .padding(start = 8.dp, top = 40.dp, end = 24.dp),
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                lineHeight = 32.sp
            )
            Text(
                text = "Proszę podać płeć:",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 20.dp, end = 40.dp),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = { setSelectedGender(Gender.MALE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGender == Gender.MALE) ButtonBlue else Color.White
                )
            ) {
                Text(
                    text = "Mężczyzna",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedGender == Gender.MALE) Color.White else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { setSelectedGender(Gender.FEMALE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGender == Gender.FEMALE) ButtonBlue else Color.White
                )
            ) {
                Text(
                    text = "Kobieta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedGender == Gender.FEMALE) Color.White else Color.Black
                )
            }
        }

        NavigationButtons(
            onBackClick = onBackClick,
            onNextClick = {
                if (selectedGender != null) {
                    viewModel?.setGender(selectedGender)
                    println("DEBUG: Сохранен пол: $selectedGender")
                    onNextClick()
                }
            },
            nextEnabled = selectedGender != null,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenderPreview() {
    TrainerTheme {
        GenderScreen(onNextClick = {}, onBackClick = {})
    }
}
