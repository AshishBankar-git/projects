package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ClinicTealContainer,
    onPrimary = ClinicTealOnContainer,
    primaryContainer = ClinicTealPrimary,
    onPrimaryContainer = ClinicTealContainer,
    secondary = ClinicSecondaryContainer,
    onSecondary = ClinicOnSecondaryContainer,
    background = Color(0xFF101415),
    surface = Color(0xFF191C1D),
    onSurface = Color(0xFFE1E3E4),
    surfaceVariant = Color(0xFF3F484B),
    onSurfaceVariant = Color(0xFFBFC8CB)
)

private val LightColorScheme = lightColorScheme(
    primary = ClinicTealPrimary,
    onPrimary = ClinicTealOnPrimary,
    primaryContainer = ClinicTealContainer,
    onPrimaryContainer = ClinicTealOnContainer,
    secondary = ClinicSecondary,
    secondaryContainer = ClinicSecondaryContainer,
    onSecondaryContainer = ClinicOnSecondaryContainer,
    tertiary = ClinicTertiary,
    tertiaryContainer = ClinicTertiaryContainer,
    onTertiaryContainer = ClinicOnTertiaryContainer,
    background = ClinicBackground,
    surface = ClinicSurface,
    onSurface = ClinicOnSurface,
    surfaceVariant = ClinicSurfaceVariant,
    onSurfaceVariant = ClinicOnSurfaceVariant,
    outline = ClinicOutline,
    outlineVariant = ClinicOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent clinic branding by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
