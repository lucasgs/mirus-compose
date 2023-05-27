package com.dendron.mirus.presentation.movie_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dendron.mirus.presentation.components.BackButton
import com.dendron.mirus.presentation.components.FavoriteAction
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import com.dendron.mirus.presentation.movie_list.components.EmptySpace
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import com.dendron.mirus.presentation.ui.theme.MyRed
import java.math.RoundingMode

@Composable
fun MovieDetailItem(
    model: MovieUiModel,
    modifier: Modifier = Modifier,
    onFavoriteClick: (MovieUiModel) -> Unit,
    onBackPressed: () -> Unit,
) {

    val movie = model.movie

    val voteAverage = remember {
        movie.voteAverage.toBigDecimal().setScale(1, RoundingMode.UP).toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MyPurple700)
    ) {
        Box(
            modifier = Modifier.height(250.dp)
        ) {
            AsyncImage(
                model = movie.backDropPath,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
            )
            BackButton(
                modifier = Modifier
                    .size(30.dp)
                    .offset(
                        x = 16.dp,
                        y = 16.dp
                    ),
            ) {
                onBackPressed()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black,
                            ),
                        ),
                    )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Transparent,
                        ),
                    )
                )
                .padding(10.dp)
        ) {
            Text(
                text = movie.title,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = Color.White,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 0.dp, vertical = 5.dp
                    )
            ) {
                model.genres.onEach { genre ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.DarkGray)
                    ) {
                        Text(
                            text = genre.name,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = Color.Gray,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                MovieVoteAverage(
                    voteAverage = voteAverage
                )
                FavoriteAction(
                    color = if (model.isFavorite) MyRed else Color.White,
                    onClick = {
                        onFavoriteClick(model)
                    },
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            EmptySpace()
            Text(
                text = movie.overview,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                lineHeight = 20.sp,
                color = Color.Gray,
            )
        }
    }
}