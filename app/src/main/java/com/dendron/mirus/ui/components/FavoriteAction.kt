package com.dendron.mirus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dendron.mirus.R

@Composable
fun FavoriteAction(color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier
            .background(Color.Transparent)
            .size(16.dp),
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = stringResource(R.string.favorite),
            tint = color,
        )
    }
}

@Preview
@Composable
fun FavoriteActionPreview() {
    FavoriteAction(
        color = Color.Green,
        modifier = Modifier
            .size(20.dp)
    ) {

    }
}