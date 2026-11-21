package com.ironlog.gym.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = Color(0xFF0D0E14),
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = OnIndigoContainerDark,
    secondary = CyanSecondaryDark,
    onSecondary = Color(0xFF0D0E14),
    secondaryContainer = CyanContainerDark,
    onSecondaryContainer = OnCyanContainerDark,
    background = BgDark,
    onBackground = TextDark,
    surface = CardDark,
    onSurface = TextDark,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = TextMutedDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark.copy(alpha = 0.6f),
    error = IronRed,
    onError = Color.White,
    errorContainer = IronRedContainerDark,
    onErrorContainer = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = OnIndigoContainerLight,
    secondary = CyanSecondary,
    onSecondary = Color.White,
    secondaryContainer = CyanContainerLight,
    onSecondaryContainer = OnCyanContainerLight,
    background = BgLight,
    onBackground = TextLight,
    surface = CardLight,
    onSurface = TextLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = TextMutedLight,
    outline = OutlineLight,
    outlineVariant = OutlineLight.copy(alpha = 0.6f),
    error = IronRed,
    onError = Color.White,
    errorContainer = IronRedContainerLight,
    onErrorContainer = IronRed
)

@Composable
fun IronLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalIronMotion provides IronMotion()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = IronTypography,
            shapes = IronShapes,
            content = content
        )
    }
}

data class ThemeController(
    val isDark: Boolean,
    val onToggle: () -> Unit
)

val LocalThemeController = staticCompositionLocalOf {
    ThemeController(isDark = true, onToggle = {})
}
