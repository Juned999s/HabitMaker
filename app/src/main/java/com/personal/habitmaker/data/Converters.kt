package com.personal.habitmaker.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTimesList(times: List<String>): String = times.joinToString(",")

    @TypeConverter
    fun toTimesList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(",")
}
