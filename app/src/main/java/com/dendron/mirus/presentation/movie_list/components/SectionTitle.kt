package com.dendron.mirus.presentation.movie_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(4.dp)) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}