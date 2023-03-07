package com.dendron.mirus.presentation.movie_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dendron.mirus.presentation.movie_detail.components.MovieDetailItem
import com.dendron.mirus.presentation.ui.theme.MyPurple200

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPurple200)
    ) {
        state.value.movie?.let { movie ->
            MovieDetailItem(movie = movie)
            if (state.value.error.isNotBlank()) {
                Text(
                    text = state.value.error,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
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
}