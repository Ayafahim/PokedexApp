package com.plcoding.jetpackcomposepokedex.screens.winPokeball

import androidx.lifecycle.ViewModel
import androidx.room.Transaction
import com.plcoding.jetpackcomposepokedex.data.models.PokeBall
import com.plcoding.jetpackcomposepokedex.db.PokeBallDao
import com.plcoding.jetpackcomposepokedex.db.PokemonDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class WinPokeballViewModel @Inject constructor(
    private val pokeBallDao: PokeBallDao
) : ViewModel() {


    private suspend fun update(pokeBall: PokeBall) {
        pokeBallDao.update(pokeBall)
    }

    suspend fun add(count: Int){
        val pokeBall = pokeBallDao.getById(1)
        pokeBall!!.count += abs(count)
        update(pokeBall!!)
    }

    // Subtract `count` from the current PokeBall count
    @Transaction
    suspend fun subtractPokeBalls(count: Int) {
        val pokeBall = pokeBallDao.getById(1)
        if (pokeBall != null || pokeBall?.count != 0) {
            pokeBall!!.count -= count
            update(pokeBall!!)
        }
    }

}