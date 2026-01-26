package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.Logic.Models.WorkoutLocation
import com.example.trainer.ui.theme.LightBlue
import com.example.trainer.ui.theme.NavigationButtons
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun Feeling(
    viewModel: OnboardingViewModel? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    var selectedLocation by remember { mutableStateOf<WorkoutLocation?>(null) }
    var selectedFrequency by remember { mutableStateOf<Int?>(null) }
    val isFormComplete = selectedLocation != null && selectedFrequency != null

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Preferencje",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Text(
                text = "Gdzie jest Ci wygodniej się uczyć?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LocationButton(
                    text = "W domu",
                    isSelected = selectedLocation == WorkoutLocation.HOME,
                    onClick = { selectedLocation = WorkoutLocation.HOME }
                )
                LocationButton(
                    text = "W silownie",
                    isSelected = selectedLocation == WorkoutLocation.GYM,
                    onClick = { selectedLocation = WorkoutLocation.GYM }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ile razy w tygodniu jesteś w stanie trenować?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FreqButton("2 razy", 2, selectedFrequency) { selectedFrequency = 2 }
                    FreqButton("3 razy", 3, selectedFrequency) { selectedFrequency = 3 }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FreqButton("4 razy", 4, selectedFrequency) { selectedFrequency = 4 }
                    FreqButton("5+ razy", 5, selectedFrequency) { selectedFrequency = 5 }
                }
            }
        }

        NavigationButtons(
            onBackClick = onBackClick,
            onNextClick = {
                viewModel?.saveWorkoutPreferences(
                    location = selectedLocation!!,
                    frequency = selectedFrequency!!
                )
                onNextClick()
            },
            nextEnabled = isFormComplete,
            nextText = "Oblicz mój plan",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun RowScope.LocationButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun RowScope.FreqButton(text: String, value: Int, selectedValue: Int?, onClick: () -> Unit) {
    val isSelected = selectedValue == value
    Box(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeelingPreview() {
    TrainerTheme {
        Feeling(onNextClick = {})
    }
}
