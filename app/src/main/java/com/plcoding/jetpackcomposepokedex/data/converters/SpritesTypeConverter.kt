package com.plcoding.jetpackcomposepokedex.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Sprites

class SpritesTypeConverter {

        @TypeConverter
        fun fromString(value: String): Sprites {
            val type = object : TypeToken<Sprites>() {}.type
            return Gson().fromJson(value, type)
        }

        @TypeConverter
        fun fromSprites(sprites: Sprites): String {
            return Gson().toJson(sprites)
        }

}