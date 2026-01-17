package com.jsjh_todaily.test_todaily_ver1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    // Surface를 사용하여 터치 인식률과 접근성 향상
    Surface(
        onClick = onClick,
        modifier = Modifier.size(width = 72.dp, height = 48.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color(0xFF615FFF) else Color(0xFFF3F3F5),
        contentColor = if (selected) Color.White else Color(0xFF717182)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = when (priority) {
                    Priority.HIGH -> "높음"
                    Priority.MEDIUM -> "보통"
                    Priority.LOW -> "낮음"
                },
                fontSize = 14.sp
            )
        }
    }
}
