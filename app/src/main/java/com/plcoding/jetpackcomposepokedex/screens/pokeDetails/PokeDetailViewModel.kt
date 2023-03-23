package com.plcoding.jetpackcomposepokedex.screens.pokeDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.db.PokemonDao
import com.plcoding.jetpackcomposepokedex.repo.PokeRepo
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokeDetailViewModel @Inject constructor(
    private val repo: PokeRepo,
    private val pokemonDao: PokemonDao
) : ViewModel() {

    val favoritePokemons = MutableLiveData<List<Pokemon>>()


    suspend fun getPokeInfo(pokeName: String): Resource<Pokemon>{
        return repo.getPokeInfo(pokeName)
    }


    suspend fun savePokemon(pokemon: Pokemon) {
        pokemonDao.insertPokemon(pokemon)
        favoritePokemons.postValue(pokemonDao.getAllPokemons().value)
    }

    suspend fun deletePokemon(pokemonId: Int) {
        pokemonDao.deletePokemon(pokemonId)
        favoritePokemons.postValue(pokemonDao.getAllPokemons().value)
    }

    suspend fun isPokemonInFavorites(pokemonId: Int): Boolean {
        return pokemonDao.isPokemonInFavorites(pokemonId) != null
    }

}