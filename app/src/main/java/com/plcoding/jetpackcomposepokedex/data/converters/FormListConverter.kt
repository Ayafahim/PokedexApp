package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Form

class FormListConverter {
    @TypeConverter
    fun fromString(value: String): List<Form> {
        val listForm = object : TypeToken<List<Form>>() {}.type
        return Gson().fromJson(value, listForm)
    }

    @TypeConverter
    fun fromList(list: List<Form>): String {
        return Gson().toJson(list)
    }
}