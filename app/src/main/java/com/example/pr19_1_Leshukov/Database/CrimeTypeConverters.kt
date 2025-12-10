package com.example.pr19_1_Leshukov.Database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID


class CrimeTypeConverters {

    @TypeConverter
    fun toUUID(uuid:String?):UUID?{
        return UUID.fromString(uuid)
    }
    @TypeConverter
    fun toUUID(uuid:UUID?):String?{
        return uuid?.toString()
    }
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date?
    {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }
}