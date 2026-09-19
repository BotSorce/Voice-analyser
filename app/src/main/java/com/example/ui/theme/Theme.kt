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

private val DarkColorScheme =
  darkColorScheme(
    primary = ElegantLavender,
    onPrimary = ElegantOnLavender,
    primaryContainer = ElegantLavenderContainer,
    onPrimaryContainer = ElegantOnLavenderContainer,
    secondary = PurpleGrey80,
    onSecondary = ElegantDarkBackground,
    tertiary = ElegantCoral,
    onTertiary = ElegantOnCoral,
    tertiaryContainer = ElegantCoralContainer,
    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceInset,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantDarkOutline,
    outlineVariant = ElegantDarkOutlineVariant,
    error = ElegantCoral,
    onError = ElegantOnCoral
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD8E4),
    background = Color(0xFFF4F5F7),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFFC4C6D0),
    outlineVariant = Color(0xFFE2E2E6),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
