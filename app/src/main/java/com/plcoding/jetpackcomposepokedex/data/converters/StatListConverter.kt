package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Stat

class StatListConverter {
    @TypeConverter
    fun fromString(value: String): List<Stat> {
        val listStat = object : TypeToken<List<Stat>>() {}.type
        return Gson().fromJson(value, listStat)
    }

    @TypeConverter
    fun fromList(list: List<Stat>): String {
        return Gson().toJson(list)
    }
}