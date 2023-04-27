package com.plcoding.jetpackcomposepokedex.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon



@Dao
interface PokemonDao {
    @Insert
    suspend fun insertPokemon(pokemon: Pokemon)

    @Query("SELECT * FROM pokemon")
    fun getAllPokemons(): LiveData<List<Pokemon>>

    @Query("DELETE FROM pokemon WHERE id = :id")
    suspend fun deletePokemon(id: Int)

    @Query("SELECT id FROM pokemon WHERE id = :pokemonId")
    suspend fun isPokemonSaved(pokemonId: Int): Int?

    @Query("SELECT COUNT(id) FROM pokemon")
    suspend fun getAmountOfPokemonsSaved(): Int

}
