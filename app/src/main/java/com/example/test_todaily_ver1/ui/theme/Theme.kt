package com.example.test_todaily_ver1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Purple500,              // #615FFF
    onPrimary = Color.White,
    primaryContainer = BackgroundLight, // #EFF6FF
    onPrimaryContainer = TextPrimary,  // #312C85
    
    secondary = Purple700,
    onSecondary = Color.White,
    
    background = BackgroundLight,     // 연보라 배경
    onBackground = TextOnCard,        // #0A0A0A
    
    surface = Surface,                // 카드 흰색
    onSurface = TextOnCard,
    onSurfaceVariant = TextTertiary,  // #717182
    
    error = PriorityHigh,
    onError = Color.White,
    
    outline = Color(0xFFE0E0E0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple500,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D3D3D),
    onPrimaryContainer = Color.White,
    
    secondary = Purple700,
    onSecondary = Color.White,
    
    background = DarkBackground,
    onBackground = DarkText,
    
    surface = DarkSurface,
    onSurface = DarkText,
    onSurfaceVariant = Color(0xFFB0B0B0),
    
    error = PriorityHigh,
    onError = Color.White,
    
    outline = Color(0xFF3D3D3D)
)

@Composable
fun Test_todaily_ver1Theme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
