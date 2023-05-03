package com.plcoding.jetpackcomposepokedex.screens.favorites

import android.app.Activity
import android.content.Intent
import android.graphics.Shader
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import coil.request.ImageRequest
import com.google.accompanist.coil.CoilImage
import com.plcoding.jetpackcomposepokedex.R
import com.plcoding.jetpackcomposepokedex.data.models.PokeBall
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.screens.pokeList.PokeListViewModel
import com.plcoding.jetpackcomposepokedex.ui.theme.RobotoCondensed
import kotlinx.coroutines.runBlocking
import nl.dionsegijn.konfetti.models.Shape

@Composable
fun ProfileScreen(navController: NavController) {

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colors.background,
            Color.White
        ),
        start = Offset(0f, 0f), // Updated start Y-coordinate to 0f
        end = Offset(0f, Float.POSITIVE_INFINITY),
        tileMode = TileMode.Clamp
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            TopSection(navController)
            Spacer(modifier = Modifier.height(10.dp))
            ProfilePic()
            Spacer(modifier = Modifier.height(15.dp))
            TrainerRank()
            WinPokeBall()
            Spacer(modifier = Modifier.height(5.dp))
            SavedPokemonsList(navController = navController)
        }
    }
}

@Composable
fun TopSection(navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.Center

    ) {

        Box(modifier = Modifier
            .offset(-(65).dp,20.dp)
            .clickable {
                navController.popBackStack()
            }
        ) {
            Image(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .align(Center)
                    .size(35.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)

            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
            contentDescription = "pokemon logo",
            modifier = Modifier
                .size(215.dp)
                .fillMaxWidth()
                .offset(x = -(20).dp, y = 10.dp)
                .align(CenterVertically)
        )

    }
}

@Composable
fun ProfilePic(viewModel: ProfileViewModel = hiltNavGraphViewModel()) {
    val defaultImageResource = R.drawable.ash
    val imageUri = remember { mutableStateOf<Uri?>(null) }


    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                imageUri.value = uri
            }
        }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            if (imageUri.value != null) {
                CoilImage(
                    data = imageUri.value!!,
                    contentDescription = "profile pic",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(140.dp)
                        .fillMaxSize()
                        .align(Center),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = defaultImageResource),
                    contentDescription = "profile pic",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(140.dp)
                        .fillMaxSize()
                        .align(Center),
                    contentScale = ContentScale.Crop
                )
            }

            // Updated the IconButton
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = CircleShape,
                color = MaterialTheme.colors.primary,
                elevation = 4.dp
            ) {
                IconButton(
                    onClick = {
                        launcher.launch(
                            Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                        )
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun TrainerRank(viewModel: ProfileViewModel = hiltNavGraphViewModel()) {
    val pokeballCount = runBlocking { viewModel.getTotalPokeBallCount() }
    val pokemonsCount = runBlocking { viewModel.getNumberOfPokemonsCaught() }
    val rank = getRank(pokemonsCount)
    val prevRank = remember { mutableStateOf(rank) }

    if (prevRank.value != rank) {
        runBlocking {
            when (rank) {

                "F" -> viewModel.addPokeBalls(PokeBall(count = 1))
                "E" -> viewModel.addPokeBalls(PokeBall(count = 1))
                "D" -> viewModel.addPokeBalls(PokeBall(count = 2))
                "C" -> viewModel.addPokeBalls(PokeBall(count = 2))
                "B" -> viewModel.addPokeBalls(PokeBall(count = 3))
                "A" -> viewModel.addPokeBalls(PokeBall(count = 5))
            }
        }
        prevRank.value = rank
    }

    val nextRank = when (rank) {
        "G" -> "F"
        "F" -> "E"
        "E" -> "D"
        "D" -> "C"
        "C" -> "B"
        "B" -> "A"
        else -> "MAX"
    }

    val progress = remember {
        val totalPokemons = 151
        (pokemonsCount.toFloat() / totalPokemons.toFloat()) * 100f
    }


    Column(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Trainer Rank", fontWeight = FontWeight.Bold)
                    Text("Next Rank: $nextRank", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress / 100f,
                    modifier = Modifier.height(5.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rank: $rank")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pokemons caught: $pokemonsCount")
                    Text("Pokeballs: $pokeballCount")
                }
            }
        }
    }

}

private fun getRank(pokemonCount: Int): String {
    return when (pokemonCount) {
        in 0..20 -> "G"
        in 21..40 -> "F"
        in 41..75 -> "E"
        in 76..120 -> "D"
        in 121..140 -> "C"
        in 141..149 -> "B"
        else -> "A"
    }
}

@Composable
fun WinPokeBall(viewModel: ProfileViewModel = hiltNavGraphViewModel()) {

    Column() {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Click on a pokeball to try and win more!",
                fontFamily = RobotoCondensed,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary)
        }
        Box(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp, start = 10.dp),
                horizontalArrangement = Arrangement.Center // Center the row contents
            ) {
                Box() {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pok__ball_icon_svg),
                            contentDescription = "pokeball",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {  }
                        )
                        Text(text = "1 PokeBall",
                            fontFamily = RobotoCondensed,
                            fontWeight = FontWeight.Normal,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary)
                    }
                }

                Spacer(modifier = Modifier.width(20.dp)) // Adjust the width for desired spacing

                Box() {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pok__ball_icon_svg),
                            contentDescription = "pokeball",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {  }
                        )
                        Text(text = "3 PokeBalls",
                            fontFamily = RobotoCondensed,
                            fontWeight = FontWeight.Normal,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary)
                    }
                }

                Spacer(modifier = Modifier.width(20.dp)) // Adjust the width for desired spacing

                Box() {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pok__ball_icon_svg),
                            contentDescription = "pokeball",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {  }
                        )
                        Text(text = "5 PokeBalls",
                            fontFamily = RobotoCondensed,
                            fontWeight = FontWeight.Normal,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary)
                    }
                }
            }
        }
    }


}


@Composable
fun SavedPokemonsList(viewModel: ProfileViewModel = hiltNavGraphViewModel(),navController: NavController) {
    val savedPokemons = viewModel.getAllFavorites().collectAsState(emptyList())

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(savedPokemons.value) { pokemon ->
            SavedPokemonItem(pokemon = pokemon, navController = navController)
        }
    }
}


@Composable
fun SavedPokemonItem(pokemon: Pokemon, viewModel: PokeListViewModel = hiltNavGraphViewModel(), navController: NavController) {

    var dominantColor by remember {
        mutableStateOf(Color.Transparent)
    }
    Box(
        contentAlignment = Center,
        modifier = Modifier
            .padding(5.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .size(160.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        MaterialTheme.colors.surface
                    )
                )
            ),


        ) {
        Column {
            // Image of pokemon
            val imageUrl =
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.id}.png"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .clickable {  navController.navigate(
                        "pokemon_details/${dominantColor.toArgb()}/${pokemon.name}"
                    ) }
            ) {
                CoilImage(
                    request = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .target {
                            viewModel.calcBCColor(it) { color ->
                                dominantColor = color
                            }
                        }
                        .build(),
                    contentDescription = pokemon.name,
                    fadeIn = true,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Center)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.scale(0.5f)
                    )
                }

            }

            Text(
                text = pokemon.name,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .align(CenterHorizontally),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}


@Preview
@Composable
fun preview() {
    ProfileScreen(rememberNavController())
}






