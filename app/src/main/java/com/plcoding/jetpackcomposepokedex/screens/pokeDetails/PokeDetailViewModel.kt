package com.plcoding.jetpackcomposepokedex.screens.pokeDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Transaction
import com.plcoding.jetpackcomposepokedex.data.models.PokeBall
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.db.PokeBallDao
import com.plcoding.jetpackcomposepokedex.db.PokemonDao
import com.plcoding.jetpackcomposepokedex.repo.PokeRepo
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokeDetailViewModel @Inject constructor(
    private val repo: PokeRepo,
    private val pokemonDao: PokemonDao,
    private val pokeBallDao: PokeBallDao
) : ViewModel() {

    val caughtPokemons = MutableLiveData<List<Pokemon>>()



    suspend fun getPokeInfo(pokeName: String): Resource<Pokemon>{
        return repo.getPokeInfo(pokeName)
    }


    suspend fun savePokemon(pokemon: Pokemon) {
        pokemonDao.insertPokemon(pokemon)
        caughtPokemons.postValue(pokemonDao.getAllPokemons().value)
    }

    suspend fun deletePokemon(pokemonId: Int) {
        pokemonDao.deletePokemon(pokemonId)
        caughtPokemons.postValue(pokemonDao.getAllPokemons().value)
    }

    suspend fun isPokemonSaved(pokemonId: Int): Boolean {
        return pokemonDao.isPokemonSaved(pokemonId) != null
    }

    suspend fun getNumberOfPokemonsCaught(): Int {
        return pokemonDao.getAmountOfPokemonsSaved()
    }

    suspend fun getRandomPokemons(count: Int): List<Pokemon> {
        val randomIds = (1..151).shuffled().take(count)
        val pokemons = mutableListOf<Pokemon>()
        for (id in randomIds) {
            val result = repo.getPokeInfo(id.toString())
            if (result is Resource.Success) {
                pokemons.add(result.data!!)
            }
        }
        return pokemons
    }

    suspend fun addPokeBalls(pokeBall: PokeBall) {
        pokeBallDao.insert(pokeBall)
    }

    private suspend fun update(pokeBall: PokeBall) {
        pokeBallDao.update(pokeBall)
    }

    suspend fun getTotalPokeBallCount(): Int {
        return pokeBallDao.getTotalCount()
    }

    // Subtract `count` from the current PokeBall count
    @Transaction
    suspend fun subtractPokeBalls(count: Int) {
        val pokeBall = pokeBallDao.getById(1)
        if (pokeBall != null) {
            pokeBall.count -= count
            update(pokeBall)
        }
    }



}