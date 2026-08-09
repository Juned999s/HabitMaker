package com.personal.habitmaker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.habitmaker.data.AppDatabase
import com.personal.habitmaker.data.Completion
import com.personal.habitmaker.data.Habit
import com.personal.habitmaker.data.HabitRepository
import com.personal.habitmaker.notifications.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitRepository(AppDatabase.getInstance(application).habitDao())
    private val scheduler = AlarmScheduler(application)

    private val today: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val habits: StateFlow<List<Habit>> = repository.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCompletions: StateFlow<List<Completion>> = repository.getCompletionsForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _streaks = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val streaks: StateFlow<Map<Long, Int>> = _streaks

    init {
        viewModelScope.launch {
            habits.collect { list ->
                val map = mutableMapOf<Long, Int>()
                list.forEach { habit -> map[habit.id] = repository.calculateStreak(habit) }
                _streaks.value = map
            }
        }
    }

    fun addHabit(name: String, colorHex: String, times: List<String>) {
        viewModelScope.launch {
            val id = repository.addHabit(Habit(name = name, colorHex = colorHex, times = times.sorted()))
            val habit = repository.getHabitById(id)
            habit?.let { scheduler.scheduleHabit(it) }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            scheduler.cancelHabit(habit)
            repository.updateHabit(habit)
            scheduler.scheduleHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            scheduler.cancelHabit(habit)
            repository.deleteHabit(habit)
        }
    }

    fun toggleCompletion(habitId: Long, time: String, done: Boolean) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, today, time, done)
            val habit = repository.getHabitById(habitId)
            habit?.let {
                val newMap = _streaks.value.toMutableMap()
                newMap[habitId] = repository.calculateStreak(it)
                _streaks.value = newMap
            }
        }
    }
}
