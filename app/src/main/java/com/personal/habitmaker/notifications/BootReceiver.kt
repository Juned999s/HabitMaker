package com.personal.habitmaker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.personal.habitmaker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).habitDao()
                val habits = dao.getAllHabits().first()
                val scheduler = AlarmScheduler(context)
                habits.forEach { scheduler.scheduleHabit(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
