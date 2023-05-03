package com.plcoding.jetpackcomposepokedex.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokeballs")
data class PokeBall(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var count: Int
)