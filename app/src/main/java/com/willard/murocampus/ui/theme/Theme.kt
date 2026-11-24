package com.willard.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulCampus,
    onPrimary = Color.White,
    secondary = AmbarUsuario,
    onSecondary = TextoAmbar,
    surfaceVariant = FondoTarjeta
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulCampusClaro,
    onPrimary = AzulOscuro,
    secondary = AmbarUsuario,
    onSecondary = TextoAmbar
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}