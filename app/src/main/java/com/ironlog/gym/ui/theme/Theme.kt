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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = Color(0xFF141A00),
    primaryContainer = ElectricLimeContainer,
    onPrimaryContainer = OnElectricLimeContainer,
    secondary = Color(0xFFC7C5D0),
    onSecondary = Color(0xFF30303A),
    background = SurfaceDark0,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark1,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDark2,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceDark3,
    error = IronRed,
    onError = Color.White,
    errorContainer = IronRedContainer,
    onErrorContainer = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricLimeLightVariant,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4F8A6),
    onPrimaryContainer = Color(0xFF182000),
    secondary = Color(0xFF5F5D6B),
    onSecondary = Color.White,
    background = SurfaceLight0,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight1,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceLight2,
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceLight3,
    error = IronRed,
    onError = Color.White
)

@Composable
fun IronLogTheme(
    darkTheme: Boolean = true, // Dark-first design
    dynamicColor: Boolean = false, // Emphasize our custom premium electric lime palette
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
