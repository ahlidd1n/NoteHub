package com.example.notehub.ViewModel

import androidx.room.TypeConverter

enum class Important{
    LOW,
    MEDIUM,
    HIGH
}

class Converters {

    @TypeConverter
    fun fromPriority(priority: Important): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Important {
        return Important.valueOf(value)
    }
}