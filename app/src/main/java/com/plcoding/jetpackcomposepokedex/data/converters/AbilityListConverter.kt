package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Ability

class AbilityListConverter {
    @TypeConverter
    fun fromString(value: String): List<Ability> {
        val listType = object : TypeToken<List<Ability>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Ability>): String {
        return Gson().toJson(list)
    }
}
