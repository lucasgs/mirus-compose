package com.dendron.mirus.presentation.movie_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.dendron.mirus.presentation.ui.theme.Yellow

@Composable
fun MovieVoteAverage(
    voteAverage: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.Star, tint = Yellow, contentDescription = "Rating"
        )
        Text(
            text = voteAverage,
            fontSize = MaterialTheme.typography.subtitle2.fontSize,
            fontWeight = FontWeight.Bold,
            color = Yellow,
        )
    }

}
