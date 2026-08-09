package com.personal.habitmaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.personal.habitmaker.data.Habit

private val presetColors = listOf("#2F6F5E", "#B5563C", "#3D5A80", "#7B5EA7", "#C08A2E", "#4C7A3D")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    existingHabit: Habit?,
    onSave: (String, String, List<String>) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)?
) {
    var name by remember { mutableStateOf(existingHabit?.name ?: "") }
    var selectedColor by remember { mutableStateOf(existingHabit?.colorHex ?: presetColors[0]) }
    var times by remember { mutableStateOf(existingHabit?.times ?: emptyList()) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingHabit == null) "New Habit" else "Edit Habit") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(20.dp).fillMaxSize()) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Text("Color", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                presetColors.forEach { hex ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (hex == selectedColor) Modifier.border(3.dp, Color.Black, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = hex }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reminder times", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(onClick = { showTimePicker = true }) { Text("+ Add") }
            }
            Spacer(Modifier.height(8.dp))
            if (times.isEmpty()) {
                Text("No reminders yet", style = MaterialTheme.typography.bodyMedium)
            } else {
                times.sorted().forEach { time ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(time, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        IconButton(onClick = { times = times.filter { it != time } }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove time")
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { if (name.isNotBlank() && times.isNotEmpty()) onSave(name.trim(), selectedColor, times) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = name.isNotBlank() && times.isNotEmpty()
            ) {
                Text("Save Habit")
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialogCompose(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                if (!times.contains(formatted)) times = times + formatted
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogCompose(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val state = rememberTimePickerState(is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = { TimePicker(state = state) }
    )
}
