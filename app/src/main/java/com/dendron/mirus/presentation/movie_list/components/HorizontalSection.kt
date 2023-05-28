package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.presentation.components.SectionTitle

@Composable
fun HorizontalSection(
    title: String,
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    rowCount: Int = 1,
    onItemClick: (Int) -> Unit
) {
    Box(modifier = modifier) {
        Column {
            SectionTitle(title = title)
            LazyHorizontalGrid(rows = GridCells.Fixed(rowCount)) {
                items(movies) { movie ->
                    MovieListItem(
                        movie = movie,
                        showTitles = false,
                        onItemClick = { onItemClick(movie.id) })
                }
            }
        }
    }
}