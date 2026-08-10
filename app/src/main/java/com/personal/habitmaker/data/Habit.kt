package com.personal.habitmaker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val times: List<String>, // "HH:mm" 24h format, sorted
    val createdAt: Long = System.currentTimeMillis(),
    val hasTimer: Boolean = false,
    val holdTimeSeconds: Int = 5,
    val relaxTimeSeconds: Int = 5,
    val totalSets: Int = 10
)
