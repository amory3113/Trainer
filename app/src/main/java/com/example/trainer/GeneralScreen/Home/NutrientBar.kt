package com.example.trainer.GeneralScreen.Home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NutrientBar(
    name: String,
    current: Int,
    target: Int,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$current / $target g",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

