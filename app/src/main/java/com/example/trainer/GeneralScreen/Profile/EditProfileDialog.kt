package com.example.trainer.GeneralScreen.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
enum class EditType {
    GOAL, ACTIVITY, WEIGHT
}
@Composable
fun EditProfileDialog(
    type: EditType,
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val goalOptions = listOf("WEIGHT_LOSS", "MAINTAIN_FITNESS", "MUSCLE_GAIN")
    val activityOptions = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")
    var selectedOption by remember { mutableStateOf(currentValue) }
    var textInput by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = when(type) {
                EditType.GOAL -> "Zmień cel"
                EditType.ACTIVITY -> "Poziom aktywności"
                EditType.WEIGHT -> "Aktualizacja wagi"
            })
        },
        text = {
            Column {
                when (type) {
                    EditType.WEIGHT -> {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = { Text("Waga (kg)") },
                            singleLine = true
                        )
                    }
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
                                    text = formatEnumText(option),
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
                Text("Zapisać")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odwolać") }
        }
    )
}

fun formatEnumText(value: String): String {
    return when(value) {
        "WEIGHT_LOSS" -> "Utrata wagi"
        "MAINTAIN_FITNESS" -> "Utrzymanie formy"
        "MUSCLE_GAIN" -> "Przyrost masy"
        "BEGINNER" -> "Niska"
        "INTERMEDIATE" -> "Średnia"
        "ADVANCED" -> "Wysoka"
        else -> value
    }
}