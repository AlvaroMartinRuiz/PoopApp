package es.uc3m.android.poopapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Extended light color scheme with our custom color mappings
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = DarkBrown, // Main brand color, used for main text and icons
    onPrimary = White,
    primaryContainer = LightBrown, // Used for buttons and interactive elements
    onPrimaryContainer = DarkBrown,
    
    // Secondary colors
    secondary = MediumBrown, // Used for emphasis and secondary actions
    onSecondary = White,
    secondaryContainer = MediumBrown.copy(alpha = 0.7f),
    onSecondaryContainer = White,
    
    // Background colors
    background = Teal, // Main app background
    onBackground = DarkBrown, // Text on background
    surface = White, // Card/component backgrounds
    onSurface = DarkBrown, // Text on card/component
    surfaceVariant = LightGray, // Alternative surface color
    onSurfaceVariant = DarkBrown.copy(alpha = 0.7f), // Muted text
    
    // Additional colors
    error = Color.Red,
    onError = White
)

// Keep dark theme consistent with light theme for now
private val DarkColorScheme = LightColorScheme

// Custom extensions to MaterialTheme for PoopApp specific colors
data class PoopAppColors(
    val staticCardBackground: Color = White,
    val popupCardBackground: Color = Teal,
    val dialogBackground: Color = Teal,
    val textFieldBackground: Color = Teal,
    val textPrimary: Color = DarkBrown,
    val textSecondary: Color = DarkBrown.copy(alpha = 0.7f),
    val buttonBackground: Color = LightBrown,
    val buttonContent: Color = DarkBrown,
    val statusGold: Color = Gold,
    val statusSilver: Color = Silver, 
    val statusBronze: Color = Bronze
)

// Define a composition local for our custom colors
val LocalPoopAppColors = staticCompositionLocalOf { PoopAppColors() }

@Composable
fun PoopAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    // Custom PoopApp colors that will be accessible through MaterialTheme
    val poopAppColors = PoopAppColors(
        staticCardBackground = White,
        popupCardBackground = Teal,
        dialogBackground = Teal,
        textFieldBackground = Teal,
        textPrimary = DarkBrown,
        textSecondary = DarkBrown.copy(alpha = 0.7f),
        buttonBackground = LightBrown,
        buttonContent = DarkBrown
    )
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar to light brown
            window.statusBarColor = LightBrown.toArgb()
            // Make status bar icons dark
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            
            // Ensure content doesn't go under the status bar
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        content = {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalPoopAppColors provides poopAppColors
            ) {
                content()
            }
        }
    )
}

// Extension property to access our custom colors from MaterialTheme
val MaterialTheme.poopAppColors: PoopAppColors
    @Composable
    get() = LocalPoopAppColors.current 