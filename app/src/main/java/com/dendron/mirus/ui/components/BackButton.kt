package com.dendron.mirus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dendron.mirus.R

@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.White),
        contentPadding = PaddingValues(4.dp),
        modifier = modifier,
        onClick = {
            onClick()
        },
    ) {
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = stringResource(R.string.go_back),
            tint = Color.White,
            modifier = Modifier.background(Color.Transparent)
        )
    }
}

@Preview
@Composable
fun Preview() {
    BackButton(
        modifier = Modifier
            .size(30.dp)
    ) {}
}