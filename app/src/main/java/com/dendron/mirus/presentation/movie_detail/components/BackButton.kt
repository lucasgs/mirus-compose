package com.dendron.mirus.presentation.movie_detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(onClick: () -> Unit) {
    OutlinedButton(
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.White),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier
            .size(24.dp)
            .offset(
                x = 10.dp,
                y = 10.dp
            ),
        onClick = {
            onClick()
        },
    ) {
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = "Go back",
            tint = Color.Black,
            modifier = Modifier.background(Color.Transparent)
        )
    }
}