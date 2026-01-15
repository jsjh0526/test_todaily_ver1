package com.jsjh_todaily.test_todaily_ver1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsjh_todaily.test_todaily_ver1.data.Priority

@Composable
fun PriorityButton(
    priority: Priority,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(52.dp)
            .height(28.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                Color(0xFF615FFF)  // 피그마 보라
            } else {
                Color(0xFFF3F3F5)  // 피그마 회색
            },
            contentColor = if (selected) {
                Color.White
            } else {
                Color(0xFF717182)  // 피그마 텍스트
            }
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            when (priority) {
                Priority.HIGH -> "높음"
                Priority.MEDIUM -> "보통"
                Priority.LOW -> "낮음"
            },
            fontSize = 14.sp
        )
    }
}
