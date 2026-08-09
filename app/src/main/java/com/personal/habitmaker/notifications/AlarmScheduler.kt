package com.personal.habitmaker.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.personal.habitmaker.data.Habit
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleHabit(habit: Habit) {
        habit.times.forEach { time ->
            val calendar = nextOccurrence(time)
            scheduleAt(habit.id, habit.name, time, calendar.timeInMillis)
        }
    }

    fun cancelHabit(habit: Habit) {
        habit.times.forEach { time -> cancelOne(habit.id, time) }
    }

    fun scheduleAt(habitId: Long, habitName: String, time: String, triggerAtMillis: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("habitId", habitId)
            putExtra("habitName", habitName)
            putExtra("time", time)
        }
        val requestCode = requestCodeFor(habitId, time)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun cancelOne(habitId: Long, time: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = requestCodeFor(habitId, time)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun nextOccurrence(time: String): Calendar {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }

    companion object {
        fun requestCodeFor(habitId: Long, time: String): Int {
            return "$habitId-$time".hashCode()
        }
    }
}
