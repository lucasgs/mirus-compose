package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dendron.mirus.presentation.movie_list.MovieUiModel

@Composable
fun TopRatedSection(
    movies: List<MovieUiModel>,
    onFavoriteClick: (MovieUiModel) -> Unit,
    onNavigateToDetailScreen: (Int) -> Unit
) {
    EmptySpace(height = 16.dp)
    HorizontalSection(
        title = "Top rated",
        movies = movies,
        modifier = Modifier.height(200.dp),
        showFavoriteAction = false,
        onFavoriteClick = { model -> onFavoriteClick(model) }
    ) { id ->
        onNavigateToDetailScreen(id)
    }
}