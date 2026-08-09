package com.personal.habitmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    background = Background,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Divider
)

@Composable
fun HabitMakerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = HabitTypography,
        content = content
    )
}
