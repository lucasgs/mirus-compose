package com.dendron.mirus.presentation.movie_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dendron.mirus.R
import com.dendron.mirus.presentation.ui.theme.Yellow

@Composable
fun MovieVoteAverage(
    voteAverage: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(4.dp)
    ) {
        Icon(
            Icons.Rounded.Star, tint = Yellow, contentDescription = stringResource(R.string.rating)
        )
        Text(
            text = voteAverage,
            fontSize = MaterialTheme.typography.subtitle2.fontSize,
            fontWeight = FontWeight.Bold,
            color = Yellow,
        )
    }

}

@Preview
@Composable
fun MovieVoteAveragePreview() {
    MovieVoteAverage(voteAverage = "5.8")
}
