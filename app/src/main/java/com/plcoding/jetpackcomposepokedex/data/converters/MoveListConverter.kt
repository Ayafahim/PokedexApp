package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Move

class MoveListConverter {
    @TypeConverter
    fun fromString(value: String): List<Move> {
        val listType = object : TypeToken<List<Move>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Move>): String {
        return Gson().toJson(list)
    }

}
