package dev.rohith.health.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3F51B5),
    onPrimary = Color(0xFFF1EBE9),
    background = Color(0xFF121A17),
    onBackground = Color(0xFFe3e3e3),
    surface = Color(0xFF060705),
    onSurface = Color(0xFFfdfdfd)
)

@Composable
fun HealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}