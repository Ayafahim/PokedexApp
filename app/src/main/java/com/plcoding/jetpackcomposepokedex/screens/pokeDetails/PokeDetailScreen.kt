package com.plcoding.jetpackcomposepokedex.screens.pokeDetails

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.google.accompanist.coil.CoilImage
import com.plcoding.jetpackcomposepokedex.R
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Type
import com.plcoding.jetpackcomposepokedex.util.Resource
import com.plcoding.jetpackcomposepokedex.util.parseStatToAbbr
import com.plcoding.jetpackcomposepokedex.util.parseStatToColor
import com.plcoding.jetpackcomposepokedex.util.parseTypeToColor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
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
                    bottom = 5.dp,
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
            dominantColor,
            navController = navController
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
        dominantColor: Color,
        navController: NavController
    ) {
        when (pokeInfo) {
            is Resource.Success -> {
                PokeCardSection(
                    pokeInfo = pokeInfo.data!!,
                    modifier = modifier
                        .offset(y = (-20).dp),
                    dominantColor,
                    navController = navController

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
        dominantColor: Color,
        navController: NavController
    ) {
        val scrollState = rememberScrollState()

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
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CatchPokemonButton(pokeInfo, dominantColor, navController = navController, pokeName = pokeInfo.name)
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
    fun CatchPokemonButton(
        pokemon: Pokemon,
        dominantColor: Color,
        navController: NavController,
        pokeName: String
    ) {
        // Create a state for tracking whether the button is pressed or not
        val isPressed = remember { mutableStateOf(false) }
        var popupControl = remember { mutableStateOf(false) }
        val pokeDetailViewModel: PokeDetailViewModel = hiltNavGraphViewModel()

        // Create a coroutine scope for adding the pokemon to the database
        val scope = rememberCoroutineScope()
        val addPokemonJob = remember { mutableStateOf<Job?>(null) }

        pokeDetailViewModel.favoritePokemons.value?.let {
            isPressed.value = it.any { it.id == pokemon.id }
        }

        // Check if the pokemon already exists in the favorites list
        val isFavorite = runBlocking { pokeDetailViewModel.isPokemonSaved(pokemon.id) }
        val pokemonsCaughtCount = runBlocking { pokeDetailViewModel.getNumberOfPokemonsCaught() }
        isPressed.value = isFavorite

        if (pokemonsCaughtCount <= 30){
            // Render the button
            Button(
                onClick = {
                    if (isPressed.value) {
                        addPokemonJob.value = scope.launch {
                            pokeDetailViewModel.deletePokemon(pokemon.id)
                            isPressed.value = false
                        }
                    } else {
                        navController.navigate("pokemon_game/${dominantColor.toArgb()}/${pokeName}")
                        isPressed.value = true
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = dominantColor,
                    contentColor = Color.White
                )
            ) {
                Text(if (isPressed.value) "Release Pokemon" else "Catch Pokemon")
            }
        }


    }

@Composable
fun CatchPokemonGameScreen(
    pokeName: String,
    navController: NavController,
    dominantColor: Color,
    modifier: Modifier = Modifier,
    viewModel: PokeDetailViewModel = hiltNavGraphViewModel(),

){
    val myPokeInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokeInfo(pokeName)
    }.value

    val scope = rememberCoroutineScope()
    val catchPokemonJob = remember { mutableStateOf<Job?>(null) }
    val randomPokemons = remember { mutableStateOf(emptyList<Pokemon>()) }
    val showSnackbar = remember { mutableStateOf(false) }
    val snackbarMessage = remember { mutableStateOf("") }
    val remainingTime = remember { mutableStateOf(5) }
    val startTimer = remember { mutableStateOf(false) }



    LaunchedEffect(pokeName)  {
        val deferredPokemons = (1..3).map {
            async { viewModel.getRandomPokemons(3) }
        }
        val pokemons = deferredPokemons.map { it.await() }
        randomPokemons.value = pokemons.flatten()
        startTimer.value = true
    }

    val radioOptions by remember(randomPokemons.value) {
        mutableStateOf(if (randomPokemons.value.isNotEmpty()) {
            listOf(
                randomPokemons.value[0].abilities[(0 until randomPokemons.value[0].abilities.size).random()].ability.name,
                randomPokemons.value[1].abilities[(0 until randomPokemons.value[1].abilities.size).random()].ability.name,
                randomPokemons.value[2].abilities[(0 until randomPokemons.value[2].abilities.size).random()].ability.name,
                myPokeInfo.data!!.abilities[(0 until myPokeInfo.data!!.abilities.size).random()].ability.name
            ).shuffled()

        } else {
            emptyList()
        })
    }

    CountdownTimer(
        remainingTime = remainingTime,
        onTimeFinished = { navController.popBackStack() },
        startTimer = startTimer.value
    )


    val (selectedOption, onOptionSelected) = remember { mutableStateOf(
        if (radioOptions.isNotEmpty()) radioOptions[0] else ""
    ) }

    fun checkSelectedAbility(selected: String): Boolean {
        return myPokeInfo.data?.abilities?.any { it.ability.name == selected } ?: false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ){
        when (myPokeInfo) {
            is Resource.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(
                            top = 20.dp + 20.dp / 2f,
                            start = 16.dp,
                            bottom = 5.dp,
                            end = 16.dp
                        )
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 10.dp)
                ) {
                    Text(
                        text = "#${myPokeInfo.data!!.id} ${myPokeInfo.data!!.name.capitalize(Locale.ROOT)}",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp
                    )
                    Text(
                        text = "Which one of these abilities does ${myPokeInfo.data!!.name} have?",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Time remaining: ${remainingTime.value}s",
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Column {
                        radioOptions.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == selectedOption),
                                        onClick = {
                                            onOptionSelected(text)
                                            if (checkSelectedAbility(text)) {
                                                catchPokemonJob.value = scope.launch {
                                                    viewModel.savePokemon(myPokeInfo.data!!)
                                                    snackbarMessage.value = "Pokemon Caught!"
                                                    showSnackbar.value = true
                                                }
                                            }else{
                                                snackbarMessage.value = "Incorrect!"
                                                showSnackbar.value = true
                                            }
                                        }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                        if (checkSelectedAbility(text)) {
                                            catchPokemonJob.value = scope.launch {
                                                viewModel.savePokemon(myPokeInfo.data!!)
                                                snackbarMessage.value = "Pokemon Caught!"
                                                showSnackbar.value = true
                                            }
                                        }else{
                                            snackbarMessage.value = "Incorrect!"
                                            showSnackbar.value = true
                                        }
                                    }
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.body1.merge(),
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                Text(
                    text = myPokeInfo.message!!,
                    color = Color.Black,
                    modifier = modifier
                )
            }
            is Resource.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .padding(
                            top = 20.dp + 20.dp / 2f,
                            start = 16.dp,
                            bottom = 16.dp,
                            end = 16.dp
                        )
                )
            }
        }
        if (showSnackbar.value) {
            LaunchedEffect(showSnackbar) {
                delay(1000)
                showSnackbar.value = false
                navController.popBackStack()
            }

            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Text(
                    text = snackbarMessage.value,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CountdownTimer(
    remainingTime: MutableState<Int>,
    onTimeFinished: () -> Unit,
    startTimer: Boolean
) {
    LaunchedEffect(startTimer) {
        if (startTimer) {
            for (i in remainingTime.value downTo 1) {
                delay(1000)
                remainingTime.value = i
            }
            onTimeFinished()
        }
    }
}


































