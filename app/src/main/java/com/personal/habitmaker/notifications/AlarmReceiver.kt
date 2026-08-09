package com.personal.habitmaker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra("habitId", -1)
        val habitName = intent.getStringExtra("habitName") ?: "Habit"
        val time = intent.getStringExtra("time") ?: return

        if (habitId == -1L) return

        NotificationHelper(context).showReminder(habitId, habitName, time)

        // Reschedule the same slot for tomorrow so it repeats daily
        val next = Calendar.getInstance().apply {
            val parts = time.split(":")
            set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(Calendar.MINUTE, parts[1].toInt())
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }

        AlarmScheduler(context).scheduleAt(habitId, habitName, time, next.timeInMillis)
    }
}
