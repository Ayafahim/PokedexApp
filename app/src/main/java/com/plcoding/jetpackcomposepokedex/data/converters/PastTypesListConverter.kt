package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PastTypesListConverter {
    @TypeConverter
    fun fromString(value: String): List<Any> {
        val listHeldItems = object : TypeToken<List<Any>>() {}.type
        return Gson().fromJson(value, listHeldItems)
    }

    @TypeConverter
    fun fromList(list: List<Any>): String {
        return Gson().toJson(list)
    }
}