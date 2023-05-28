package com.dendron.mirus.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dendron.mirus.presentation.ui.theme.MyPurple700

@Composable
fun CenteredMessageBox(
    modifier: Modifier = Modifier,
    message: String,
    textColor: Color = Color.White
) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier.fillMaxHeight()
    ) {
        Text(
            text = message,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(MyPurple700)
                .padding(horizontal = 20.dp)
                .align(Alignment.Center)
        )
    }
}
