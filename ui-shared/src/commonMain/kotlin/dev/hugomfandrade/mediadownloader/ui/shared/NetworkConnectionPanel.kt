package dev.hugomfandrade.mediadownloader.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoNetworkPanel(text: String, modifier: Modifier = Modifier, show: Boolean = true) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(if (show) Dp.Unspecified else 0.dp)
            .background(Color(0xFF1E1E1E))
            .wrapContentHeight()
    )
}