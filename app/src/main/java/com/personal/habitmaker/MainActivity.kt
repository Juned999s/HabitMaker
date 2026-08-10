package com.personal.habitmaker

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.personal.habitmaker.data.Habit
import com.personal.habitmaker.ui.AddEditHabitScreen
import com.personal.habitmaker.ui.HabitViewModel
import com.personal.habitmaker.ui.HomeScreen
import com.personal.habitmaker.ui.theme.HabitMakerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HabitViewModel by lazy {
        ViewModelProvider(this)[HabitViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }

        setContent {
            HabitMakerTheme {
                var editingHabit by remember { mutableStateOf<Habit?>(null) }
                var showAddEdit by remember { mutableStateOf(false) }

                if (showAddEdit) {
                    AddEditHabitScreen(
                        existingHabit = editingHabit,
                        onSave = { name, color, times, hasTimer, holdTime, relaxTime, totalSets ->
                            val current = editingHabit
                            if (current != null) {
                                viewModel.updateHabit(
                                    current.copy(
                                        name = name,
                                        colorHex = color,
                                        times = times.sorted(),
                                        hasTimer = hasTimer,
                                        holdTimeSeconds = holdTime,
                                        relaxTimeSeconds = relaxTime,
                                        totalSets = totalSets
                                    )
                                )
                            } else {
                                viewModel.addHabit(name, color, times, hasTimer, holdTime, relaxTime, totalSets)
                            }
                            showAddEdit = false
                            editingHabit = null
                        },
                        onCancel = {
                            showAddEdit = false
                            editingHabit = null
                        },
                        onDelete = editingHabit?.let { habit ->
                            {
                                viewModel.deleteHabit(habit)
                                showAddEdit = false
                                editingHabit = null
                            }
                        }
                    )
                } else {
                    HomeScreen(
                        viewModel = viewModel,
                        onAddHabit = {
                            editingHabit = null
                            showAddEdit = true
                        },
                        onEditHabit = { habit ->
                            editingHabit = habit
                            showAddEdit = true
                        }
                    )
                }
            }
        }
    }
}
