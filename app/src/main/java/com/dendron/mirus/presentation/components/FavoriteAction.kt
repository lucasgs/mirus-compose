package com.dendron.mirus.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
            contentDescription = "Favorite",
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