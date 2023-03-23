package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Species

class SpeciesTypeConverter {
    @TypeConverter
    fun fromSpecies(value: Species): String {
        val gson = Gson()
        val type = object : TypeToken<Species>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toSpecies(value: String): Species {
        val gson = Gson()
        val type = object : TypeToken<Species>() {}.type
        return gson.fromJson(value, type)
    }
}
