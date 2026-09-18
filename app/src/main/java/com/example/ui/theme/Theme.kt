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
    primary = BlueprintSecondary,
    onPrimary = Color.White,
    primaryContainer = BlueprintPrimary,
    onPrimaryContainer = Color.White,
    secondary = SafetyAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = SafetyAmberLight,
    tertiary = PassGreen,
    background = SlateDark,
    surface = SlateNavy,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = SlateMedium,
    onSurfaceVariant = Color(0xFFE2E8F0),
    outline = SlateMedium
)

private val LightColorScheme = lightColorScheme(
    primary = BlueprintPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = BlueprintPrimary,
    secondary = SafetyAmber,
    onSecondary = Color.White,
    secondaryContainer = SafetyAmberLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = PassGreen,
    background = ConcreteSurface,
    surface = ConcreteCard,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryDark,
    outline = ConcreteBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent professional civil laboratory colors
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
