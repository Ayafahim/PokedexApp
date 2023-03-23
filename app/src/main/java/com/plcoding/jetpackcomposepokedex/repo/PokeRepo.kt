package com.plcoding.jetpackcomposepokedex.repo

import com.plcoding.jetpackcomposepokedex.data.remote.PokeApi
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responses.PokemonList
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokeRepo @Inject constructor(
    private val api: PokeApi
) {
    suspend fun getPokeList(limit: Int, offset: Int): Resource<PokemonList>{
        val response = try {
            api.getPokeList(limit,offset)
        }catch (e: Exception){
            return Resource.Error("Error, Fck")
        }
        return Resource.Success(response)
    }

    suspend fun getPokeInfo(pokemonName: String): Resource<Pokemon>{
        val response = try {
            api.getPokeInfo(pokemonName)
        }catch (e: Exception){
            return Resource.Error("Error, Fck")
        }
        return Resource.Success(response)
    }

}