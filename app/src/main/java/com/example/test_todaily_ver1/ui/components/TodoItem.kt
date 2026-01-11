package com.example.test_todaily_ver1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test_todaily_ver1.data.Priority
import com.example.test_todaily_ver1.data.Todo
import com.example.test_todaily_ver1.ui.theme.PriorityHigh
import com.example.test_todaily_ver1.ui.theme.PriorityLow
import com.example.test_todaily_ver1.ui.theme.PriorityMedium

@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
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
                onCheckedChange = { onToggle() }
            )
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.content,
                    fontSize = 16.sp,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todo.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Spacer(Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    todo.tags.forEach { tag ->
                        Text(
                            "#$tag",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    "삭제",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
