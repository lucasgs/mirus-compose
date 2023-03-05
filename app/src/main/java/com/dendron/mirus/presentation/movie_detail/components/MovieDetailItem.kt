package com.dendron.mirus.presentation.movie_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dendron.mirus.domain.model.Movie

@Composable
fun MovieDetailItem(
    movie: Movie
){
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
        ) {
            AsyncImage(
                model = movie.backDropPath,
                contentDescription = movie.title,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxWidth()
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
            ) {
                Text(
                    text = movie.title,
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    color = Color.White
                )
                Text(
                    text = movie.releaseDate.toString(),
                    fontSize = MaterialTheme.typography.subtitle2.fontSize,
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray,
                )
                Text(
                    text = movie.popularity.toString(),
                    fontSize = MaterialTheme.typography.subtitle2.fontSize,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.onSecondary,
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = movie.overview,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                    color = Color.White
                )

            }
        }
    }
}