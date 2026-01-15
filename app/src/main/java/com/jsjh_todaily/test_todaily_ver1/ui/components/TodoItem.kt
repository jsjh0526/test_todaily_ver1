package com.jsjh_todaily.test_todaily_ver1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import com.jsjh_todaily.test_todaily_ver1.data.Priority
import com.jsjh_todaily.test_todaily_ver1.data.Todo
import com.jsjh_todaily.test_todaily_ver1.ui.theme.PriorityHigh
import com.jsjh_todaily.test_todaily_ver1.ui.theme.PriorityLow
import com.jsjh_todaily.test_todaily_ver1.ui.theme.PriorityMedium
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF615FFF)
                )
            )
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.content,
                    fontSize = 16.sp,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todo.isCompleted) {
                        Color(0xFF717182)
                    } else {
                        if (isDarkTheme) Color.White else Color(0xFF0A0A0A)
                    }
                )
                
                Spacer(Modifier.height(8.dp))
                
                // 첫 번째 줄: 우선순위, 마감일
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 우선순위
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            null,
                            modifier = Modifier.size(6.dp),
                            tint = when (todo.priority) {
                                Priority.HIGH -> PriorityHigh
                                Priority.MEDIUM -> PriorityMedium
                                Priority.LOW -> PriorityLow
                            }
                        )
                        Text(
                            when (todo.priority) {
                                Priority.HIGH -> "높음"
                                Priority.MEDIUM -> "보통"
                                Priority.LOW -> "낮음"
                            },
                            fontSize = 14.sp,
                            color = Color(0xFF717182)
                        )
                    }
                    
                    // 마감일 표시
                    if (todo.dueDate != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF717182)
                            )
                            Text(
                                SimpleDateFormat("MM/dd HH:mm", Locale.KOREAN)
                                    .format(Date(todo.dueDate!!)),
                                fontSize = 13.sp,
                                color = Color(0xFF717182)
                            )
                        }
                    }
                }
                
                // 두 번째 줄: 알림들
                if (todo.reminderTimes.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        todo.reminderTimes.sortedDescending().take(2).forEach { reminderTime ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFFFF9800)
                                )
                                Text(
                                    SimpleDateFormat("MM/dd HH:mm", Locale.KOREAN)
                                        .format(Date(reminderTime)),
                                    fontSize = 13.sp,
                                    color = Color(0xFF717182)
                                )
                            }
                        }
                        // 2개 넘으면 +N 표시
                        if (todo.reminderTimes.size > 2) {
                            Text(
                                "+${todo.reminderTimes.size - 2}",
                                fontSize = 12.sp,
                                color = Color(0xFF717182)
                            )
                        }
                    }
                }
                
                // 세 번째 줄: 태그
                if (todo.tags.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        todo.tags.forEach { tag ->
                            Text(
                                "#$tag",
                                fontSize = 14.sp,
                                color = Color(0xFF615FFF)
                            )
                        }
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    "삭제",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}
