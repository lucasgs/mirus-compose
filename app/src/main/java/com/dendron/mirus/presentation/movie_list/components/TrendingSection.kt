package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dendron.mirus.domain.model.Movie

@Composable
fun TrendingSection(
    movies: List<Movie>,
    onNavigateToDetailScreen: (Int) -> Unit
) {
    EmptySpace(height = 16.dp)
    HorizontalSection(
        title = "Trending",
        movies = movies,
        modifier = Modifier.height(300.dp),
    ) { id ->
        onNavigateToDetailScreen(id)
    }
}