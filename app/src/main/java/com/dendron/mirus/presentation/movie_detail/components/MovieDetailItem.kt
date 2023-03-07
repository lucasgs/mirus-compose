package com.dendron.mirus.presentation.movie_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.presentation.movie_list.components.EmptySpace
import java.math.RoundingMode

@Composable
fun MovieDetailItem(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    val voteAverage = remember {
        movie.voteAverage.toBigDecimal().setScale(1, RoundingMode.UP).toString()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black,
                    ),
                )
            ),
    ) {
        Column {
            AsyncImage(
                model = movie.backDropPath,
                contentDescription = movie.title,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(300.dp)
            )
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = movie.title,
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    color = Color.White
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 5.dp)
                ) {
                    movie.genres.onEach { genre ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.DarkGray)
                        ) {
                            Text(
                                text = genre.name,
                                fontSize = MaterialTheme.typography.body2.fontSize,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(5.dp)
                            )
                        }
                    }
                }
                MovieVoteAverage(
                    voteAverage = voteAverage
                )
                EmptySpace()
                Text(
                    text = movie.overview,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    lineHeight = 20.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}