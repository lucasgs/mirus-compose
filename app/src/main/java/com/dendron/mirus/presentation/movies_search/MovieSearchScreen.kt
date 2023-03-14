package com.dendron.mirus.presentation.movies_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dendron.mirus.presentation.movie_list.components.EmptySpace
import com.dendron.mirus.presentation.movie_list.components.VerticalSection
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import kotlinx.coroutines.launch

@Composable
fun MovieSearchScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MovieSearchViewModel = hiltViewModel()
) {
    var text by remember { mutableStateOf("") }
    val state = viewModel.movies.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    fun navigateToDetailScreen(movieId: Int) {
        coroutineScope.launch {
            navController.navigate(Screen.MovieDetailScreen.route + "/$movieId")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MyPurple700)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            EmptySpace(height = 16.dp)
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    placeholder = {
                        Text(text = "Search")
                    },
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .background(Color.White)
                )
                OutlinedButton(
                    shape = CircleShape,
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier
                        .size(42.dp),
                    onClick = {
                        viewModel.searchMovie(text)
                        text = ""
                    },
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier
                            .background(Color.Transparent)
                    )
                }
            }
            if (state.value.movies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "Nothing to show yet :)",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                VerticalSection(
                    title = "",
                    movies = state.value.movies,
                    showTitles = false,
                    showFavoriteAction = false,
                    onFavoriteClick = { },
                    onItemClick = { movieId ->
                        navigateToDetailScreen(movieId)
                    }
                )
            }
        }
        if (state.value.error.isNotEmpty()) {
            Text(
                text = state.value.error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if (state.value.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}