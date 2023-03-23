package com.plcoding.jetpackcomposepokedex.screens.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.db.PokemonDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val pokemonDao: PokemonDao
) : ViewModel() {

    fun getAllFavorites(): LiveData<List<Pokemon>> {
        return pokemonDao.getAllPokemons()
    }
}
