package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.GameIndice


class GameIndiceListConverter {
    @TypeConverter
    fun fromString(value: String): List<GameIndice> {
        val listGameIndice = object : TypeToken<List<GameIndice>>() {}.type
        return Gson().fromJson(value, listGameIndice)
    }

    @TypeConverter
    fun fromList(list: List<GameIndice>): String {
        return Gson().toJson(list)
    }
}