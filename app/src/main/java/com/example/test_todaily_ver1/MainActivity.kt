package com.example.test_todaily_ver1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.test_todaily_ver1.ui.screens.*
import com.example.test_todaily_ver1.ui.theme.Test_todaily_ver1Theme
import com.example.test_todaily_ver1.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Test_todaily_ver1Theme {
                var showSplash by remember { mutableStateOf(true) }
                
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: TodoViewModel = viewModel()
    
    val isDarkMode = isSystemInDarkTheme()
    val backgroundLight = com.example.test_todaily_ver1.ui.theme.BackgroundLight
    val backgroundLight2 = com.example.test_todaily_ver1.ui.theme.BackgroundLight2
    val darkBackground = com.example.test_todaily_ver1.ui.theme.DarkBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                if (isDarkMode) {
                    drawRect(darkBackground)
                } else {
                    val angleRad = 11.736f * (Math.PI / 180f).toFloat()
                    val x = size.width
                    val y = x * kotlin.math.tan(angleRad)
                    
                    val gradient = androidx.compose.ui.graphics.Brush.linearGradient(
                        colorStops = arrayOf(
                            0.085f to backgroundLight,
                            0.915f to backgroundLight2
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(x, y)
                    )
                    drawRect(brush = gradient)
                }
            }
    ) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    listOf(
                        BottomNavItem("home", "홈", Icons.Default.Home),
                        BottomNavItem("list", "리스트", Icons.Default.List),
                        BottomNavItem("settings", "설정", Icons.Default.Settings)
                    ).forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") { HomeScreen(viewModel) }
                composable("list") { ListScreen(viewModel) }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
