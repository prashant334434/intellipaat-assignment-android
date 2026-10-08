package com.example.learning.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Slate50,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoPrimaryContainerDark,
    secondary = CyanSecondary,
    onSecondary = Slate50,
    secondaryContainer = CyanSecondaryContainer,
    onSecondaryContainer = CyanSecondaryContainerDark,
    tertiary = EmeraldSuccess,
    onTertiary = Slate50,
    tertiaryContainer = EmeraldSuccessContainer,
    onTertiaryContainer = EmeraldSuccessContainerDark,
    background = Slate50,
    onBackground = Slate900,
    surface = Slate50,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate300,
    error = RoseError,
    onError = Slate50,
    errorContainer = RoseErrorContainer,
    onErrorContainer = RoseErrorContainerDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = Slate950,
    primaryContainer = IndigoPrimaryContainerDark,
    onPrimaryContainer = IndigoPrimaryContainer,
    secondary = CyanSecondaryDark,
    onSecondary = Slate950,
    secondaryContainer = CyanSecondaryContainerDark,
    onSecondaryContainer = CyanSecondaryContainer,
    tertiary = EmeraldSuccessDark,
    onTertiary = Slate950,
    tertiaryContainer = EmeraldSuccessContainerDark,
    onTertiaryContainer = EmeraldSuccessContainer,
    background = Slate950,
    onBackground = Slate50,
    surface = Slate900,
    onSurface = Slate50,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = Slate700,
    outlineVariant = Slate600,
    error = RoseErrorDark,
    onError = Slate950,
    errorContainer = RoseErrorContainerDark,
    onErrorContainer = RoseErrorContainer,
)

@Composable
fun LearningDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our curated palette for rich brand consistency
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
