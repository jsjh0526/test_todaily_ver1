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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jsjh_todaily.test_todaily_ver1.ui.screens.*
import com.jsjh_todaily.test_todaily_ver1.ui.theme.Test_todaily_ver1Theme
import com.jsjh_todaily.test_todaily_ver1.viewmodel.TodoViewModel
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : ComponentActivity() {
    

    
    // 업데이트 결과 처리 런처
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            // 업데이트 취소 또는 실패 - 강제 업데이트이므로 앱 종료
            finish()
        }
    }
    
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
        
        // In-App Update 체크 (강제 업데이트)
        checkForUpdate()
        
        // 알림 권한 요청
        requestNotificationPermission()
        
        // 정확한 알람 권한 요청 (Android 12+)
        requestExactAlarmPermission()
        
        setContent {
            val todoViewModel: TodoViewModel = viewModel()
            val themeMode by todoViewModel.themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()
            
            // 테마 결정
            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemInDarkTheme  // "system"
            }
            
            Test_todaily_ver1Theme(darkTheme = darkTheme) {
                // 시스템바 색상 동기화
                val view = LocalView.current
                val colorScheme = MaterialTheme.colorScheme
                
                SideEffect {
                    val window = (view.context as ComponentActivity).window
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    // 상태바/네비게이션바 아이콘 색상 설정 (다크 모드에서 밝게, 라이트 모드에서 어둡게)
                    insetsController.isAppearanceLightStatusBars = !darkTheme
                    insetsController.isAppearanceLightNavigationBars = !darkTheme
                }
                
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
    
    override fun onResume() {
        super.onResume()
        // 앱 재시작시 미완료 업데이트 체크
        checkPendingUpdate()
    }
    
    // 업데이트 체크
    private fun checkForUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        updateOptions
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    // 미완료 업데이트 체크
    private fun checkPendingUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        updateOptions
                    )
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
    
    // 사용자 선택 테마 가져오기
    val themeMode by viewModel.themeMode.collectAsState()
    val systemInDarkTheme = isSystemInDarkTheme()
    
    // 테마 결정 (사용자 선택 우선!)
    val isDarkMode = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> systemInDarkTheme  // "system"
    }
    
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
