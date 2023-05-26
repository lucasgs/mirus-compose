package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dendron.mirus.domain.model.Movie

@Composable
fun VerticalSection(
    modifier: Modifier = Modifier,
    title: String = "",
    movies: List<Movie>,
    showTitles: Boolean = true,
    onItemClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        SectionTitle(title = title)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize()
        ) {
            items(movies) { movie ->
                MovieListItem(
                    movie = movie,
                    showTitles = showTitles,
                    onItemClick = { item ->
                        onItemClick(item.id)
                    }
                )
            }
        }
    }
}