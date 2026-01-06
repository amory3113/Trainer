package com.example.trainer.GeneralScreen.Stats

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    dataPoints: List<Float>,
    graphColor: Color = Color(0xFF2196F3)
) {
    if (dataPoints.isEmpty()) return

    val spacing = 100f
    val maxVal = dataPoints.maxOrNull() ?: 1f
    val minVal = dataPoints.minOrNull() ?: 0f

    val range = if (maxVal - minVal == 0f) 1f else (maxVal - minVal)
    val displayMax = maxVal + (range * 0.1f)
    val displayMin = (minVal - (range * 0.1f)).coerceAtLeast(0f)
    val displayRange = displayMax - displayMin

    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.GRAY
            textAlign = Paint.Align.LEFT
            textSize = 32f
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {

        val width = size.width
        val height = size.height
        val distanceX = width / (dataPoints.size - 1).coerceAtLeast(1)

        val gridLines = 4
        for (i in 0..gridLines) {
            val yRatio = i.toFloat() / gridLines
            val y = height * yRatio
            val value = displayMax - (displayRange * yRatio)

            // Линия
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", value),
                0f,
                y - 10f,
                textPaint
            )
        }

        if(dataPoints.size == 1){
            val y = height - ((dataPoints.first() - displayMin) / displayRange * height)
            drawCircle(
                color = graphColor,
                radius = 6.dp.toPx(),
                center = Offset(width / 2, y)
            )
            return@Canvas
        }

        val strokePath = Path().apply {
            val firstY = height - ((dataPoints.first() - displayMin) / displayRange * height)
            moveTo(0f, firstY)

            for (i in 1 until dataPoints.size) {
                val currentVal = dataPoints[i]
                val prevVal = dataPoints[i - 1]

                val currentX = i * distanceX
                val currentY = height - ((currentVal - displayMin) / displayRange * height)
                val prevX = (i - 1) * distanceX
                val prevY = height - ((prevVal - displayMin) / displayRange * height)

                val controlPoint1 = Offset(prevX + (currentX - prevX) / 2, prevY)
                val controlPoint2 = Offset(prevX + (currentX - prevX) / 2, currentY)

                cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, currentX, currentY)
            }
        }

        val fillPath = Path()
        fillPath.addPath(strokePath)
        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    graphColor.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                endY = height
            )
        )

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        for (i in dataPoints.indices) {
            val x = i * distanceX
            val y = height - ((dataPoints[i] - displayMin) / displayRange * height)

            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = graphColor,
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}