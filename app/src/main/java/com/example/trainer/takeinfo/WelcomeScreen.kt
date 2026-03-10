package com.example.trainer.takeinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.ButtonBlue
import com.example.trainer.ui.theme.GradientBackground
import com.example.trainer.ui.theme.LightBlue
import com.example.trainer.ui.theme.OpenSans
import com.example.trainer.ui.theme.TrainerTheme

@Composable
fun WelcomeScreen(
    onNextClick: () -> Unit
) {
    GradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                Text(
                    text = "Witamy w programie treningu personalnego!",
                    color = Color.Black,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    lineHeight = 42.sp,
                    fontFamily = OpenSans
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Osiągnij swoją wymarzoną sylwetkę dzięki inteligentnym algorytmom dostosowanym do Twojego zdrowia i celów.",
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = OpenSans
                )

                Spacer(modifier = Modifier.height(40.dp))

                FeatureItem(
                    icon = Icons.Default.FitnessCenter,
                    title = "Spersonalizowane plany",
                    description = "Treningi dobrane do Twoich możliwości."
                )
                Spacer(modifier = Modifier.height(20.dp))
                FeatureItem(
                    icon = Icons.Default.Restaurant,
                    title = "Śledzenie kalorii",
                    description = "Automatyczne wyliczanie zapotrzebowania BMR/TDEE."
                )
                Spacer(modifier = Modifier.height(20.dp))
                FeatureItem(
                    icon = Icons.Default.Favorite,
                    title = "Zdrowie na 1. miejscu",
                    description = "Filtrowanie ćwiczeń pod kątem kontuzji."
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Rozpocznij",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = OpenSans
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ButtonBlue,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold, fontFamily = OpenSans,
                color = Color.Black
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray, fontFamily = OpenSans,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    TrainerTheme {
        WelcomeScreen(onNextClick = {})
    }
}