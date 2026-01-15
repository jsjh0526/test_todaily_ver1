package com.jsjh_todaily.test_todaily_ver1

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jsjh_todaily.test_todaily_ver1.ui.screens.*
import com.jsjh_todaily.test_todaily_ver1.ui.theme.Test_todaily_ver1Theme
import com.jsjh_todaily.test_todaily_ver1.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
    
    // 알림 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // 권한 거부됨 - 사용자에게 안내
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 알림 권한 요청
        requestNotificationPermission()
        
        // 정확한 알람 권한 요청 (Android 12+)
        requestExactAlarmPermission()
        
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
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한 있음
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // 권한 설명 필요
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // 권한 요청
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // 정확한 알람 권한 요청
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
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
    val backgroundLight = com.jsjh_todaily.test_todaily_ver1.ui.theme.BackgroundLight
    val backgroundLight2 = com.jsjh_todaily.test_todaily_ver1.ui.theme.BackgroundLight2
    val darkBackground = com.jsjh_todaily.test_todaily_ver1.ui.theme.DarkBackground

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
                composable("settings") { SettingsScreen(viewModel) }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
