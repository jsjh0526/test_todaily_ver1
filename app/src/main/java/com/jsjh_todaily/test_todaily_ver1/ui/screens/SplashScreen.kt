package com.jsjh_todaily.test_todaily_ver1.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF615FFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 아이콘 카드
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color.Black.copy(alpha = 0.25f),
                        ambientColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .background(
                        color = Color(0xFF7D74FF),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 흰색 테두리 동그라미
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(
                            width = 4.dp,
                            color = Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // 체크마크 (Canvas)
                    Canvas(
                        modifier = Modifier.size(36.dp)
                    ) {
                        val checkColor = Color.White
                        val strokeWidth = 5.dp.toPx()
                        
                        // 체크마크 ✓
                        drawLine(
                            color = checkColor,
                            start = Offset(size.width * 0.2f, size.height * 0.5f),
                            end = Offset(size.width * 0.45f, size.height * 0.73f),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                        
                        drawLine(
                            color = checkColor,
                            start = Offset(size.width * 0.45f, size.height * 0.73f),
                            end = Offset(size.width * 0.85f, size.height * 0.27f),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // 텍스트
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "할 일 관리",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                
                Text(
                    text = "당신의 하루를 계획하세요",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // 하단 점 3개
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .background(
                            color = if (index == 0) Color.White else Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
