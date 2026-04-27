package com.noteflow.app.features.goals.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noteflow.app.features.goals.domain.model.Goal

private val SurfaceColor = Color(0xFF1C1B1B)
private val SurfaceHigh = Color(0xFF2A2A2A)
private val PrimaryColor = Color(0xFFCABEFF)
private val AccentColor = Color(0xFF8A70FF)
private val OnSurfaceVariant = Color(0xFFC8C5CD)
private val HighPriorityColor = Color(0xFFFF6B6B)

@Composable
fun GoalAddEditDialog(
    editingGoal: Goal?,
    onConfirm: (title: String, description: String, targetDate: Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember(editingGoal) { mutableStateOf(editingGoal?.title ?: "") }
    var description by remember(editingGoal) { mutableStateOf(editingGoal?.description ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var targetDate by remember(editingGoal) { mutableStateOf(editingGoal?.targetDate) }

    val formatted = remember(targetDate) {
        if (targetDate != null) {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            sdf.format(java.util.Date(targetDate!!))
        } else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        title = {
            Text(
                if (editingGoal != null) "تعديل الهدف" else "هدف جديد",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الهدف") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = OnSurfaceVariant,
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = OnSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف (اختياري)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = OnSurfaceVariant,
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = OnSurfaceVariant
                    )
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatted ?: "تحديد تاريخ (اختياري)")
                }
                if (targetDate != null) {
                    TextButton(
                        onClick = { targetDate = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("مسح التاريخ", color = HighPriorityColor, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) onConfirm(title.trim(), description.trim(), targetDate)
                }
            ) {
                Text("حفظ", color = PrimaryColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = OnSurfaceVariant)
            }
        }
    )

    if (showDatePicker) {
        GoalDatePickerDialog(
            initialDate = targetDate,
            onDateSelected = { targetDate = it; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDatePickerDialog(
    initialDate: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate ?: System.currentTimeMillis()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onDateSelected(it) }
            }) {
                Text("تأكيد", color = PrimaryColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = OnSurfaceVariant)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = SurfaceColor
        )
    ) {
        DatePicker(
            state = state,
            colors = DatePickerDefaults.colors(
                containerColor = SurfaceColor,
                titleContentColor = Color.White,
                headlineContentColor = PrimaryColor,
                weekdayContentColor = OnSurfaceVariant,
                subheadContentColor = OnSurfaceVariant,
                navigationContentColor = Color.White,
                yearContentColor = Color.White,
                currentYearContentColor = PrimaryColor,
                selectedYearContentColor = Color(0xFF1C0062),
                selectedYearContainerColor = PrimaryColor,
                dayContentColor = Color.White,
                selectedDayContentColor = Color(0xFF1C0062),
                selectedDayContainerColor = PrimaryColor,
                todayContentColor = PrimaryColor,
                todayDateBorderColor = PrimaryColor
            )
        )
    }
}
