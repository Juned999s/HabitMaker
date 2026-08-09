package com.personal.habitmaker.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitRepository(private val dao: HabitDao) {

    fun getAllHabits(): Flow<List<Habit>> = dao.getAllHabits()

    fun getCompletionsForDate(date: String): Flow<List<Completion>> = dao.getCompletionsForDate(date)

    suspend fun addHabit(habit: Habit): Long = dao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = dao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) {
        dao.deleteCompletionsForHabit(habit.id)
        dao.deleteHabit(habit)
    }

    suspend fun getHabitById(id: Long) = dao.getHabitById(id)

    suspend fun toggleCompletion(habitId: Long, date: String, time: String, done: Boolean) {
        if (done) {
            dao.upsertCompletion(Completion(habitId, date, time, true))
        } else {
            dao.deleteCompletion(habitId, date, time)
        }
    }

    /** Counts consecutive fully-completed days ending today (today is allowed to still be in progress). */
    suspend fun calculateStreak(habit: Habit): Int {
        if (habit.times.isEmpty()) return 0

        val completions = dao.getCompletionsForHabit(habit.id)
        val completedDates = completions
            .groupBy { it.date }
            .filter { (_, list) -> habit.times.all { t -> list.any { c -> c.time == t } } }
            .keys

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        var streak = 0

        var dateStr = sdf.format(cal.time)
        if (!completedDates.contains(dateStr)) {
            // today not finished yet - don't break the streak, just start counting from yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            dateStr = sdf.format(cal.time)
            if (completedDates.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
