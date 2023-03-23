package com.plcoding.jetpackcomposepokedex.data.remote.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.plcoding.jetpackcomposepokedex.data.converters.*

@Entity
@TypeConverters(
    AbilityListConverter::class, FormListConverter::class, GameIndiceListConverter::class,
    MoveListConverter::class, PastTypesListConverter::class,
    StatListConverter::class, TypeListConverter::class, SpeciesTypeConverter::class,SpritesTypeConverter::class)
data class Pokemon(
    val abilities: List<Ability>,
    val base_experience: Int,
    val forms: List<Form>,
    val game_indices: List<GameIndice>,
    val height: Int,
    val held_items: List<Any>,
    @PrimaryKey
    val id: Int,
    val is_default: Boolean,
    val location_area_encounters: String,
    val moves: List<Move>,
    val name: String,
    val order: Int,
    val past_types: List<Any>,
    val species: Species,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val weight: Int
)