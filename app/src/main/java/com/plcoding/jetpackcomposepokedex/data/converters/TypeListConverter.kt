package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Type

class TypeListConverter {
    @TypeConverter
    fun fromString(value: String): List<Type> {
        val listType = object : TypeToken<List<Type>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Type>): String {
        return Gson().toJson(list)
    }
}
