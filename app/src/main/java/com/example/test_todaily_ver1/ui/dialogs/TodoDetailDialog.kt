package com.example.test_todaily_ver1.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.test_todaily_ver1.data.Priority
import com.example.test_todaily_ver1.data.Todo
import com.example.test_todaily_ver1.ui.components.PriorityButton
import com.example.test_todaily_ver1.ui.components.TagChip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit,
    onDelete: () -> Unit
) {
    var content by remember { mutableStateOf(todo.content) }
    var selectedPriority by remember { mutableStateOf(todo.priority) }
    var tags by remember { mutableStateOf(todo.tags) }
    var tagInput by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(todo.description ?: "") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "할일 수정",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "닫기")
                    }
                }

                // 할일 내용
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("할 일") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // 우선순위
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "우선순위",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PriorityButton(
                            priority = Priority.HIGH,
                            selected = selectedPriority == Priority.HIGH,
                            onClick = { selectedPriority = Priority.HIGH }
                        )
                        PriorityButton(
                            priority = Priority.MEDIUM,
                            selected = selectedPriority == Priority.MEDIUM,
                            onClick = { selectedPriority = Priority.MEDIUM }
                        )
                        PriorityButton(
                            priority = Priority.LOW,
                            selected = selectedPriority == Priority.LOW,
                            onClick = { selectedPriority = Priority.LOW }
                        )
                    }
                }

                // 태그
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "태그",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (tags.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tags.forEach { tag ->
                                TagChip(
                                    tag = tag,
                                    onRemove = { tags = tags - tag }
                                )
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        placeholder = { Text("태그 입력 후 완료...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (tagInput.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val newTag = tagInput.trim()
                                        if (newTag.isNotBlank() && !tags.contains(newTag)) {
                                            tags = tags + newTag
                                            tagInput = ""
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        "태그 추가",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = {
                                val newTag = tagInput.trim()
                                if (newTag.isNotBlank() && !tags.contains(newTag)) {
                                    tags = tags + newTag
                                    tagInput = ""
                                }
                            }
                        )
                    )
                }

                // 메모
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("메모") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 5
                )

                Divider()

                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 삭제 버튼
                    OutlinedButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                MaterialTheme.colorScheme.error
                            )
                        )
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("삭제")
                    }

                    // 저장 버튼
                    Button(
                        onClick = {
                            val updatedTodo = todo.copy(
                                content = content,
                                priority = selectedPriority,
                                tags = tags,
                                description = description.ifBlank { null }
                            )
                            onSave(updatedTodo)
                            onDismiss()
                        },
                        enabled = content.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("저장")
                    }
                }
            }
        }
    }
}
