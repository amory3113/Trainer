package com.example.trainer.GeneralScreen.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Типы того, что мы можем редактировать
enum class EditType {
    GOAL, ACTIVITY, WEIGHT
}

@Composable
fun EditProfileDialog(
    type: EditType,
    currentValue: String, // Текущее значение (чтобы знать, что выбрано)
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit // Возвращаем новое значение (всегда как строку, разберем во VM)
) {
    // Варианты выбора для радио-кнопок
    val goalOptions = listOf("WEIGHT_LOSS", "MAINTAIN_FITNESS", "MUSCLE_GAIN")
    val activityOptions = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")

    // Состояние выбора
    var selectedOption by remember { mutableStateOf(currentValue) }
    var textInput by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = when(type) {
                EditType.GOAL -> "Изменить цель"
                EditType.ACTIVITY -> "Уровень активности"
                EditType.WEIGHT -> "Обновить вес"
            })
        },
        text = {
            Column {
                when (type) {
                    // Если меняем Вес - показываем поле ввода
                    EditType.WEIGHT -> {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = { Text("Вес (кг)") },
                            singleLine = true
                        )
                    }
                    // Если меняем Цель или Активность - показываем список (RadioButtons)
                    else -> {
                        val options = if (type == EditType.GOAL) goalOptions else activityOptions

                        options.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (option == selectedOption),
                                        onClick = { selectedOption = option }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (option == selectedOption),
                                    onClick = { selectedOption = option }
                                )
                                Text(
                                    text = formatEnumText(option), // Красивый текст (см. ниже)
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (type == EditType.WEIGHT) {
                    onConfirm(textInput)
                } else {
                    onConfirm(selectedOption)
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

// Вспомогательная функция для красивого текста
fun formatEnumText(value: String): String {
    return when(value) {
        "WEIGHT_LOSS" -> "Похудение"
        "MAINTAIN_FITNESS" -> "Поддержка"
        "MUSCLE_GAIN" -> "Набор массы"
        "BEGINNER" -> "Низкая"
        "INTERMEDIATE" -> "Средняя"
        "ADVANCED" -> "Высокая"
        else -> value
    }
}