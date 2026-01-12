package com.example.test_todaily_ver1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    // Primary (피그마 정확)
    primary = Purple500,                 // #615FFF 우선순위
    onPrimary = Color.White,
    primaryContainer = BackgroundLight,  // #EFF6FF 배경
    onPrimaryContainer = TextTitle,      // #312C85 제목
    
    // Secondary (네비게이션)
    secondary = Purple600,               // #4F39F6 선택된 네비
    onSecondary = Color.White,
    
    // Background
    background = BackgroundLight,        // #EFF6FF
    onBackground = TextTitle,            // #312C85
    
    // Surface (카드)
    surface = Color.White,
    onSurface = TextOnCard,              // #0A0A0A
    onSurfaceVariant = TextSubtitle,     // #4A5565
    surfaceVariant = SurfaceGray,        // #F3F3F5
    
    // Outline
    outline = TextPlaceholder,           // #99A1AF
    
    // Error
    error = PriorityHigh,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple500,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D3D3D),
    onPrimaryContainer = Color.White,
    
    secondary = Purple600,
    onSecondary = Color.White,
    
    background = DarkBackground,
    onBackground = DarkText,
    
    surface = DarkSurface,
    onSurface = DarkText,
    onSurfaceVariant = Color(0xFFB0B0B0),
    surfaceVariant = Color(0xFF3D3D3D),
    
    outline = Color(0xFF5D5D5D),
    
    error = PriorityHigh,
    onError = Color.White
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
