package dev.hugomfandrade.mediadownloader.android.utils

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
        secondary = context.themeColor(androidx.appcompat.R.attr.colorPrimaryDark),
        tertiary = context.themeColor(androidx.appcompat.R.attr.colorAccent),
        surface = context.themeColor(android.R.attr.colorBackground),
        onPrimary = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography.copy(
            titleLarge = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Typeface.create("sans-serif-condensed", Typeface.BOLD))
            )
        ),
        content = content
    )
}

