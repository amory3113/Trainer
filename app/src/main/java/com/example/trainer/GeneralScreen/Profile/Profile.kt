package com.example.trainer.GeneralScreen.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainer.ui.theme.GradientBackground
import com.example.trainer.data.UserEntity

@Composable
fun Profile(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var editType by remember { mutableStateOf(EditType.WEIGHT) }
    var editValue by remember { mutableStateOf("") }

    GradientBackground {
        if (userProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2196F3))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(userProfile!!)

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Moje dane")
                CardSection {
                    ProfileOptionItem(
                        icon = Icons.Default.MonitorWeight,
                        title = "Waga",
                        value = "${userProfile!!.weight} kg",
                        onClick = {
                            editType = EditType.WEIGHT
                            editValue = userProfile!!.weight.toString()
                            showDialog = true
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    ProfileOptionItem(
                        icon = Icons.Default.Height,
                        title = "Wysokość",
                        value = "${userProfile!!.height} cm",
                        showArrow = true,
                        onClick = {
                            editType = EditType.HEIGHT
                            editValue = userProfile!!.height.toString()
                            showDialog = true
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileOptionItem(
                        icon = Icons.Default.Cake,
                        title = "Wiek",
                        value = "${userProfile!!.age} lat",
                        showArrow = true,
                        onClick = {
                            editType = EditType.AGE
                            editValue = userProfile!!.age.toString()
                            showDialog = true
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    ProfileOptionItem(
                        icon = Icons.Default.Transgender,
                        title = "Płeć",
                        value = formatGender(userProfile!!.gender),
                        showArrow = false
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Ustawienia programu")
                CardSection {
                    ProfileOptionItem(
                        icon = Icons.Default.Flag,
                        title = "Cel",
                        value = formatGoal(userProfile!!.goal),
                        onClick = {
                            editType = EditType.GOAL
                            editValue = userProfile!!.goal
                            showDialog = true
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    ProfileOptionItem(
                        icon = Icons.Default.FitnessCenter,
                        title = "Aktywność",
                        value = formatActivity(userProfile!!.activityLevel),
                        onClick = {
                            editType = EditType.ACTIVITY
                            editValue = userProfile!!.activityLevel
                            showDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Aplikacja")
                CardSection {
                    ProfileOptionItem(
                        icon = Icons.Default.Info,
                        title = "O aplikacji",
                        value = "Wersja 1.0",
                        showArrow = false
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.clearData { onLogout() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Outlined.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zresetuj postęp i wyjdź")
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    if (showDialog) {
        EditProfileDialog(
            type = editType,
            currentValue = editValue,
            onDismiss = { showDialog = false },
            onConfirm = { newValue ->
                viewModel.updateProfile(editType, newValue)
                showDialog = false
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    type: EditType,
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedValue by remember { mutableStateOf(currentValue) }

    val title = when (type) {
        EditType.WEIGHT -> "Zmień wagę"
        EditType.HEIGHT -> "Zmień wzrost"
        EditType.AGE -> "Zmień wiek"
        EditType.GOAL -> "Zmień cel"
        EditType.ACTIVITY -> "Zmień aktywność"
    }

    val suffix = when (type) {
        EditType.WEIGHT -> "kg"
        EditType.HEIGHT -> "cm"
        EditType.AGE -> "lat/lata"
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (type == EditType.GOAL) {
                    val options = mapOf(
                        "WEIGHT_LOSS" to "Utrata wagi",
                        "MUSCLE_GAIN" to "Przyrost masy",
                        "MAINTAIN_FITNESS" to "Utrzymanie formy"
                    )
                    options.forEach { (key, label) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = (selectedValue == key),
                                    onClick = { selectedValue = key },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedValue == key),
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = label)
                        }
                    }

                } else if (type == EditType.ACTIVITY) {
                    val options = mapOf(
                        "BEGINNER" to "Niska",
                        "INTERMEDIATE" to "Średnia",
                        "ADVANCED" to "Wysoka"
                    )
                    options.forEach { (key, label) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = (selectedValue == key),
                                    onClick = { selectedValue = key },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedValue == key),
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = label)
                        }
                    }

                } else {
                    OutlinedTextField(
                        value = selectedValue,
                        onValueChange = { selectedValue = it },
                        label = { Text(suffix) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedValue) },
                ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

@Composable
fun ProfileHeader(user: UserEntity) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .border(2.dp, Color(0xFF2196F3), CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Użytkownik",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun CardSection(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    value: String,
    showArrow: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun formatGender(gender: String): String {
    return when(gender) {
        "MALE" -> "Mężczyzna"
        "FEMALE" -> "Kobieta"
        else -> gender
    }
}

fun formatGoal(goal: String): String {
    return when(goal) {
        "WEIGHT_LOSS" -> "Utrata wagi"
        "MUSCLE_GAIN" -> "Przyrost masy"
        "MAINTAIN_FITNESS" -> "Utrzymanie formy"
        else -> goal
    }
}

fun formatActivity(activity: String): String {
    return when(activity) {
        "BEGINNER" -> "Niska"
        "INTERMEDIATE" -> "Średnia"
        "ADVANCED" -> "Wysoka"
        else -> activity
    }
}