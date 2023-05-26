package com.dendron.mirus.presentation.movie_favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dendron.mirus.R
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPurple700)
            .padding(4.dp)
    ) {
        AnimatedVisibility(visible = !state.value.isLoading) {
            Column {
                EmptySpace(height = 16.dp)
                VerticalSection(title = stringResource(R.string.favorites),
                    movies = state.value.movies,
                    showTitles = false,
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