package com.dendron.mirus.presentation.movie_favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dendron.mirus.R
import com.dendron.mirus.presentation.components.CenteredMessageBox
import com.dendron.mirus.presentation.components.VerticalSection
import com.dendron.mirus.presentation.movie_list.components.EmptySpace
import com.dendron.mirus.presentation.navigation.Screen
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        AnimatedVisibility(visible = !state.value.isLoading) {
            val movies = state.value.movies
            if (movies.isEmpty()) {
                CenteredMessageBox(message = stringResource(R.string.no_favorites))
            } else {
                Column {
                    EmptySpace(height = 16.dp)
                    VerticalSection(title = stringResource(R.string.favorites),
                        movies = movies,
                        showTitles = false,
                        onItemClick = { movieId ->
                            navigateToDetailScreen(movieId)
                        })
                }
            }
        }
        if (state.value.error.isNotEmpty()) {
            CenteredMessageBox(message = state.value.error, textColor = Color.Red)
        }
        if (state.value.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}