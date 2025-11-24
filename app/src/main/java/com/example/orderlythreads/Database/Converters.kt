package com.example.orderlythreads.Database

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    // --- Automatic Date Tracker ---
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // --- Automatic Enum for Production Status ---
    @TypeConverter
    fun fromStatus(value: ProductionStatus): String {
        return value.name
    }

    @TypeConverter
    fun toStatus(value: String): ProductionStatus {
        return ProductionStatus.valueOf(value)
    }
}