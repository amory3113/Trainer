package com.example.trainer.GeneralScreen.Stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    dataPoints: List<Float>,
    lineColor: Color = Color(0xFF2196F3)
) {
    if (dataPoints.isEmpty()) return

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {

        if(dataPoints.size == 1){
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = Offset(size.width / 2, size.height / 2)
            )
            return@Canvas
        }

        val distance = size.width / (dataPoints.size - 1)
        val maxVal = dataPoints.maxOrNull() ?: 1f
        val minVal = dataPoints.minOrNull() ?: 0f

        // Нормализуем высоту, чтобы график выглядел красиво
        // (чтобы линия не была прижата к краям)
        val range = if(maxVal - minVal == 0f) 1f else (maxVal - minVal)
        val heightPadding = size.height * 0.2f // 20% отступа сверху и снизу
        val drawingHeight = size.height - (heightPadding * 2)

        val points = dataPoints.mapIndexed { index, value ->
            val normalizedValue = (value - minVal) / range // от 0 до 1
            val y = size.height - (heightPadding + normalizedValue * drawingHeight)
            val x = index * distance
            Offset(x, y.toFloat())
        }

        // Рисуем линию
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                // Используем cubicTo для плавности линий (кривые Безье)
                // Или просто lineTo для острых углов. Сделаем плавно:
                val prev = points[i - 1]
                val current = points[i]
                val controlPoint1 = Offset(prev.x + (current.x - prev.x) / 2, prev.y)
                val controlPoint2 = Offset(prev.x + (current.x - prev.x) / 2, current.y)
                cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, current.x, current.y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Рисуем точки
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}