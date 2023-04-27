package com.plcoding.jetpackcomposepokedex.screens.pokeDetails

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.CoilImage
import com.plcoding.jetpackcomposepokedex.R
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Type
import com.plcoding.jetpackcomposepokedex.util.Resource
import com.plcoding.jetpackcomposepokedex.util.parseStatToAbbr
import com.plcoding.jetpackcomposepokedex.util.parseStatToColor
import com.plcoding.jetpackcomposepokedex.util.parseTypeToColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.round

@Composable
fun PokeDetailScreen(
    dominantColor: Color,
    pokeName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokeImageSize: Dp = 200.dp,
    viewModel: PokeDetailViewModel = hiltNavGraphViewModel()
) {
    val pokeInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokeInfo(pokeName)
    }.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ) {

        PokeTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        )
        PokeDetailStateWrap(
            pokeInfo = pokeInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokeImageSize / 2f,
                    start = 16.dp,
                    bottom = 16.dp,
                    end = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokeImageSize / 2f,
                    start = 16.dp,
                    bottom = 16.dp,
                    end = 16.dp
                ),
            dominantColor
        )

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (pokeInfo is Resource.Success) {
                pokeInfo.data?.sprites?.let {
                    CoilImage(
                        data = it.front_default,
                        contentDescription = pokeInfo.data.name,
                        fadeIn = true,
                        modifier = Modifier
                            .size(pokeImageSize)
                            .offset(y = topPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PokeTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {

        Box(modifier = Modifier
            .offset(35.dp, 35.dp)
            .clickable {
                navController.popBackStack()
            }
        ) {
            Image(
                imageVector = Icons.Default.ArrowBackIos,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(30.dp),
                colorFilter = ColorFilter.tint(Color.White)

            )
        }
    }
}

    @Composable
    fun PokeDetailStateWrap(
        pokeInfo: Resource<Pokemon>,
        modifier: Modifier = Modifier,
        loadingModifier: Modifier = Modifier,
        dominantColor: Color
    ) {
        when (pokeInfo) {
            is Resource.Success -> {
                PokeCardSection(
                    pokeInfo = pokeInfo.data!!,
                    modifier = modifier
                        .offset(y = (-20).dp),
                    dominantColor

                )
            }
            is Resource.Error -> {
                Text(
                    text = pokeInfo.message!!,
                    color = Color.Black,
                    modifier = modifier
                )
            }
            is Resource.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = loadingModifier
                )
            }
        }

    }

    @Composable
    fun PokeCardSection(
        pokeInfo: Pokemon,
        modifier: Modifier = Modifier,
        dominantColor: Color
    ) {
        val scrollState = rememberScrollState()
        val favorites = remember { mutableStateOf(listOf<Pokemon>()) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .offset(y = 100.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "#${pokeInfo.id} ${pokeInfo.name.capitalize(Locale.ROOT)}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
            PokeTypeSection(types = pokeInfo.types)
            PokeMeasurementsSection(pokeWeight = pokeInfo.weight, pokeHeight = pokeInfo.height)
            PokemonBaseStats(pokeInfo = pokeInfo)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddFavoriteButton(pokeInfo, dominantColor)
            }

        }

    }

    @Composable
    fun PokeTypeSection(types: List<Type>) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            for (type in types) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape)
                        .background(parseTypeToColor(type))
                        .height(35.dp)
                ) {
                    Text(
                        text = type.type.name.capitalize(Locale.ROOT),
                        color = Color.White,
                        fontSize = 18.sp
                    )

                }
            }

        }
    }

    @Composable
    fun PokeDetailMeasurementItem(
        value: Float,
        unit: String,
        icon: Painter,
        modifier: Modifier = Modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
        ) {
            Icon(painter = icon, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$value$unit",
                color = Color.Black
            )
        }

    }

    @Composable
    fun PokeMeasurementsSection(
        pokeWeight: Int,
        pokeHeight: Int,
        sectionHeight: Dp = 80.dp
    ) {
        val pokeWeightInKg = remember {
            round(pokeWeight * 100f) / 1000f
        }
        val pokeHeightInM = remember {
            round(pokeHeight * 100f) / 1000f
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            PokeDetailMeasurementItem(
                value = pokeWeightInKg,
                unit = "kg",
                icon = painterResource(id = R.drawable.ic_weight),
                modifier = Modifier.weight(1f)
            )
            Spacer(
                modifier = Modifier
                    .size(width = 1.dp, height = sectionHeight)
                    .background(Color.LightGray)
            )
            PokeDetailMeasurementItem(
                value = pokeHeightInM,
                unit = "M",
                icon = painterResource(id = R.drawable.ic_height),
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    fun PokemonStat(
        statName: String,
        statValue: Int,
        statMaxValue: Int,
        statColor: Color,
        height: Dp = 28.dp,
        animDuration: Int = 1000,
        animDelay: Int = 0
    ) {
        var animationPlayed by remember {
            mutableStateOf(false)
        }
        val curPercent = animateFloatAsState(
            targetValue = if (animationPlayed) {
                statValue / statMaxValue.toFloat()
            } else 0f,
            animationSpec = tween(
                animDuration,
                animDelay
            )
        )
        LaunchedEffect(key1 = true) {
            animationPlayed = true
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(CircleShape)
                .background(
                    if (isSystemInDarkTheme()) {
                        Color(0xFF505050)
                    } else {
                        Color.LightGray
                    }
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(curPercent.value)
                    .clip(CircleShape)
                    .background(statColor)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = statName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = (curPercent.value * statMaxValue).toInt().toString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun PokemonBaseStats(
        pokeInfo: Pokemon,
        animDelayPerItem: Int = 100
    ) {
        val maxBaseStat = remember {
            pokeInfo.stats.maxOf { it.base_stat }
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Base stats:",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))

            for (i in pokeInfo.stats.indices) {
                val stat = pokeInfo.stats[i]
                PokemonStat(
                    statName = parseStatToAbbr(stat),
                    statValue = stat.base_stat,
                    statMaxValue = maxBaseStat,
                    statColor = parseStatToColor(stat),
                    animDelay = i * animDelayPerItem
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    @Composable
    fun AddFavoriteButton(
        pokemon: Pokemon,
        dominantColor: Color
    ) {
        // Create a state for tracking whether the button is pressed or not
        val isPressed = remember { mutableStateOf(false) }
        val pokeDetailViewModel: PokeDetailViewModel = hiltNavGraphViewModel()

        // Create a coroutine scope for adding the pokemon to the database
        val scope = rememberCoroutineScope()
        val addPokemonJob = remember { mutableStateOf<Job?>(null) }

        pokeDetailViewModel.favoritePokemons.value?.let {
            isPressed.value = it.any { it.id == pokemon.id }
        }

        // Check if the pokemon already exists in the favorites list
        val isFavorite = runBlocking { pokeDetailViewModel.isPokemonSaved(pokemon.id) }
        isPressed.value = isFavorite

        // Render the button
        Button(
            onClick = {
                if (isPressed.value) {
                    addPokemonJob.value = scope.launch {
                        pokeDetailViewModel.deletePokemon(pokemon.id)
                        isPressed.value = false
                    }
                } else {
                    addPokemonJob.value = scope.launch {
                        try {
                            pokeDetailViewModel.savePokemon(pokemon)
                            isPressed.value = true
                        } catch (e: Exception) {
                            if (e is SQLiteConstraintException) {
                                println("Pokemon already exists in favorites")
                                return@launch
                            } else {
                                throw e
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = dominantColor,
                contentColor = Color.White
            )
        ) {
            Text(if (isPressed.value) "Remove from favorites" else "Add to favorites")
        }
    }




















