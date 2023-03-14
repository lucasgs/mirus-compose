package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dendron.mirus.presentation.movie_list.MovieUiModel

@Composable
fun VerticalSection(
    title: String,
    movies: List<MovieUiModel>,
    modifier: Modifier = Modifier,
    showTitles: Boolean = true,
    showFavoriteAction: Boolean = true,
    onFavoriteClick: (MovieUiModel) -> Unit,
    onItemClick: (Int) -> Unit
) {
    Box(modifier = modifier) {
        Column {
            SectionTitle(title = title)
            LazyVerticalGrid(
//                columns = GridCells.Adaptive(minSize = 100.dp),
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies) { model ->
                    MovieListItem(
                        model = model,
                        showTitles = showTitles,
                        showFavoriteAction = showFavoriteAction,
                        onFavoriteClick = {
                            onFavoriteClick(model)
                        },
                        onItemClick = { movie ->
                            onItemClick(movie.id)
                        }
                    )
                }
            }
        }
    }
}