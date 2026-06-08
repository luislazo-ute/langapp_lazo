package com.langapp.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = Accent, onPrimary = AccentOnDark,
    secondary = AccentLight, onSecondary = AccentOnDark,
    background = Background, onBackground = TextPrimary,
    surface = Surface, onSurface = TextPrimary,
    surfaceVariant = Surface2, onSurfaceVariant = TextSecondary,
    outline = Border, error = Error, onError = Color.White,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun LangAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}