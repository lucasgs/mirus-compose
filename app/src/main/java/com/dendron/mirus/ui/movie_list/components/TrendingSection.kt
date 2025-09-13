package com.dendron.mirus.ui.movie_list.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dendron.mirus.R
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.ui.components.EmptySpace
import com.dendron.mirus.ui.movie_list.components.HorizontalSection

@Composable
fun TrendingSection(
    movies: List<Movie>,
    onNavigateToDetailScreen: (Int) -> Unit
) {
    EmptySpace(height = 16.dp)
    HorizontalSection(
        title = stringResource(R.string.trending),
        movies = movies,
        modifier = Modifier.height(300.dp),
    ) { id ->
        onNavigateToDetailScreen(id)
    }
}