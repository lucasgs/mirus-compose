package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dendron.mirus.presentation.movie_list.MovieUiModel

@Composable
fun TrendingSection(
    movies: List<MovieUiModel>,
    onFavoriteClick: (MovieUiModel) -> Unit,
    onNavigateToDetailScreen: (Int) -> Unit
) {
    EmptySpace(height = 16.dp)
    HorizontalSection(
        title = "Trending",
        movies = movies,
        modifier = Modifier.height(300.dp),
        showFavoriteAction = false,
        onFavoriteClick = { model -> onFavoriteClick(model) }
    ) { id ->
        onNavigateToDetailScreen(id)
    }
}