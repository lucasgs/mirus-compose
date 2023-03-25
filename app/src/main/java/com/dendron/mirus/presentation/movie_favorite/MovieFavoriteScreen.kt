package com.dendron.mirus.presentation.movie_favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import com.dendron.mirus.presentation.movie_list.components.EmptySpace
import com.dendron.mirus.presentation.movie_list.components.VerticalSection
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import kotlinx.coroutines.launch

@Composable
fun MovieFavoriteScreen(
    navController: NavHostController, viewModel: MovieFavoriteViewModel = hiltViewModel()
) {
    val state = viewModel.movies.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    fun navigateToDetailScreen(movieId: Int) {
        coroutineScope.launch {
            navController.navigate(Screen.MovieDetailScreen.route + "/$movieId")
        }
    }

    fun onFavoriteClick(model: MovieUiModel) {
        viewModel.toggleMovieAsFavorite(model)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPurple700)
            .padding(4.dp)
    ) {
        AnimatedVisibility(visible = !state.value.isLoading) {
            Column {
                EmptySpace(height = 16.dp)
                VerticalSection(title = "Favorites",
                    movies = state.value.movies,
                    showTitles = false,
                    showFavoriteAction = false,
                    onFavoriteClick = { model ->
                        onFavoriteClick(model)
                    },
                    onItemClick = { movieId ->
                        navigateToDetailScreen(movieId)
                    })
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
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}