package com.plcoding.jetpackcomposepokedex.screens.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.db.PokemonDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pokemonDao: PokemonDao
) : ViewModel() {


    fun getAllFavorites(): Flow<List<Pokemon>> {
        return pokemonDao.getAllPokemons().asFlow()
    }

    fun <T> LiveData<T>.asFlow(): Flow<T> = callbackFlow {
        val observer = Observer<T> { newValue -> offer(newValue) }
        observeForever(observer)
        awaitClose { removeObserver(observer) }
    }
}