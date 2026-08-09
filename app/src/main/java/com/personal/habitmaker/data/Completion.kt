package com.personal.habitmaker.data

import androidx.room.Entity

@Entity(tableName = "completions", primaryKeys = ["habitId", "date", "time"])
data class Completion(
    val habitId: Long,
    val date: String, // yyyy-MM-dd
    val time: String, // HH:mm
    val done: Boolean = true
)
