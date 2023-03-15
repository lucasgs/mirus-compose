package com.dendron.mirus.presentation.movie_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.dendron.mirus.presentation.components.rememberLifecycleEvent
import com.dendron.mirus.presentation.movie_list.components.EmptySpace
import com.dendron.mirus.presentation.movie_list.components.HorizontalSection
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import kotlinx.coroutines.launch

@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val discoverState = viewModel.discoverMovies.collectAsStateWithLifecycle()
    val topRatedState = viewModel.topRatedMovies.collectAsStateWithLifecycle()
    val trendingState = viewModel.trendingMovies.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isError = viewModel.isError.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            viewModel.refresh()
        }
    }

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
//        AnimatedVisibility(visible = !isLoading.value) {
            Column {
                EmptySpace(height = 16.dp)
                HorizontalSection(
                    title = "Top rated",
                    movies = topRatedState.value.movies,
                    modifier = Modifier.height(200.dp),
                    onFavoriteClick = { model -> onFavoriteClick(model) }
                ) { id ->
                    navigateToDetailScreen(id)
                }
                EmptySpace()
                HorizontalSection(
                    title = "Discover",
                    movies = discoverState.value.movies,
                    modifier = Modifier.height(200.dp),
                    onFavoriteClick = { model -> onFavoriteClick(model) }
                ) { id ->
                    navigateToDetailScreen(id)
                }
                EmptySpace()
                HorizontalSection(
                    title = "Trending",
                    movies = trendingState.value.movies,
                    modifier = Modifier.height(300.dp),
                    onFavoriteClick = { model -> onFavoriteClick(model) }
                ) { id ->
                    navigateToDetailScreen(id)
                }
            }
//        }
        if (isError.value.isNotEmpty()) {
            Text(
                text = isError.value.joinToString(),
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

