package dev.hugomfandrade.mediadownloader.android.utils

import android.content.Context
import android.util.TypedValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun Context.themeColor(attr: Int): Color {
    val typedValue = TypedValue()
    val wasResolved = theme.resolveAttribute(attr, typedValue, true)
    return if (wasResolved) Color(typedValue.data) else Color.Unspecified
}

@Composable
fun LegacyAppCompatTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val colorScheme = lightColorScheme(
        primary = context.themeColor(androidx.appcompat.R.attr.colorPrimary),
        secondary = context.themeColor(androidx.appcompat.R.attr.colorAccent),
        surface = context.themeColor(android.R.attr.colorBackground),
        onPrimary = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

