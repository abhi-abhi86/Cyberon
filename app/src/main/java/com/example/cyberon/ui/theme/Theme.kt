package com.example.cyberon.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = CyberBlack,
    surface = CyberSurface,
    onPrimary = CyberBlack,
    onSecondary = CyberTextPrimary,
    onTertiary = CyberTextPrimary,
    onBackground = CyberTextPrimary,
    onSurface = CyberTextPrimary,
)

@Composable
fun CyberonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // We prefer dark theme for this cyber aesthetic
    content: @Composable () -> Unit
) {
    // Force dark theme for the cyber look
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
