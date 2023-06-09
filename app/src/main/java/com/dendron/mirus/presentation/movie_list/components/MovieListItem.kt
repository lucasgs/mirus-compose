package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.presentation.ui.theme.MyPurple700

@Composable
fun MovieListItem(
    movie: Movie,
    showTitles: Boolean = true,
    onItemClick: (Movie) -> Unit
) {
    Card(
        modifier = Modifier
            .height(150.dp)
            .clickable { onItemClick(movie) }
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .background(MyPurple700)
        ) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = movie.title,
                contentScale = ContentScale.FillHeight,
            )
            if (showTitles) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                            )
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                    )
                }
            }
        }
    }
}