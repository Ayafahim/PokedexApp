package com.plcoding.jetpackcomposepokedex.screens.pokeList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.plcoding.jetpackcomposepokedex.data.models.PokedexListEntry
import com.plcoding.jetpackcomposepokedex.repo.PokeRepo
import com.plcoding.jetpackcomposepokedex.util.Constants.PAGE_SIZE
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokeListViewModel @Inject constructor(
    private val repo: PokeRepo
) : ViewModel() {

    private var currentPage = 0

    var pokeList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf(" ")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var  cachePokeList = listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPaginatedPokemon()
    }

    fun SeachList(search: String){
        val listToSearch = if(isSearchStarting){
            pokeList.value
        }else{
            cachePokeList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (search.isEmpty()){
                pokeList.value = cachePokeList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val result = listToSearch.filter {
                it.pokemonName.contains(search.trim(), ignoreCase = true) || it.number.toString() == search.trim()
            }
            if (isSearchStarting){
                cachePokeList = pokeList.value
                isSearchStarting = false
            }
            pokeList.value = result
            isSearching.value = true
        }
    }

    fun loadPaginatedPokemon(){
        isLoading.value = true
        viewModelScope.launch {
            val result = repo.getPokeList(PAGE_SIZE, currentPage * PAGE_SIZE)
            when(result){
                is Resource.Success -> {
                    endReached.value = currentPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if(entry.url.endsWith("/")){
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        }else{
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.capitalize(Locale.ROOT),url,number.toInt())
                    }
                    currentPage++

                    loadError.value = " "
                    isLoading.value = false
                    pokeList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
            }
        }
    }

    fun calcBCColor(drawable: Drawable, onFinish: (Color) -> Unit){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate{ palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}