package com.plcoding.jetpackcomposepokedex.screens.pokeDetails

import androidx.lifecycle.ViewModel
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.repo.PokeRepo
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokeDetailViewModel @Inject constructor(
    private val repo: PokeRepo
) : ViewModel() {

    suspend fun getPokeInfo(pokeName: String): Resource<Pokemon>{
        return repo.getPokeInfo(pokeName)
    }

}