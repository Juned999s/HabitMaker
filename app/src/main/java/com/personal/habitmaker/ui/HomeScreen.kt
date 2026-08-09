package com.personal.habitmaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitmaker.data.Habit
import com.personal.habitmaker.ui.theme.Divider
import com.personal.habitmaker.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (Habit) -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val completions by viewModel.todayCompletions.collectAsState()
    val streaks by viewModel.streaks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Habits", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabit) {
                Icon(Icons.Default.Add, contentDescription = "Add habit")
            }
        }
    ) { padding ->
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No habits yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text("Tap + to add your first one", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    val doneTimes = completions.filter { it.habitId == habit.id }.map { it.time }.toSet()
                    HabitCard(
                        habit = habit,
                        doneTimes = doneTimes,
                        streak = streaks[habit.id] ?: 0,
                        onToggleTime = { time, done -> viewModel.toggleCompletion(habit.id, time, done) },
                        onClick = { onEditHabit(habit) }
                    )
                }
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    doneTimes: Set<String>,
    streak: Int,
    onToggleTime: (String, Boolean) -> Unit,
    onClick: () -> Unit
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(accentColor))
                Spacer(Modifier.width(10.dp))
                Text(habit.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (streak > 0) {
                    Text("\uD83D\uDD25 $streak", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                habit.times.forEach { time ->
                    val isDone = doneTimes.contains(time)
                    TimeChip(time = time, done = isDone, accentColor = accentColor) {
                        onToggleTime(time, !isDone)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeChip(time: String, done: Boolean, accentColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (done) accentColor.copy(alpha = 0.15f) else Divider)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (done) {
            Icon(Icons.Default.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(time, style = MaterialTheme.typography.labelSmall, color = if (done) accentColor else TextSecondary)
    }
}
