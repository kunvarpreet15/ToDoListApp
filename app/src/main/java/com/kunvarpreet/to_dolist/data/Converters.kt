package com.kunvarpreet.to_dolist.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromRepeatInterval(interval: RepeatInterval?): String {
        return interval?.name ?: RepeatInterval.NONE.name
    }

    @TypeConverter
    fun toRepeatInterval(value: String?): RepeatInterval {
        if (value.isNullOrEmpty()) return RepeatInterval.NONE
        return try {
            RepeatInterval.valueOf(value)
        } catch (e: Exception) {
            RepeatInterval.NONE
        }
    }
}
