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
    var dueDate by remember { mutableStateOf(todo.dueDate) }
    var reminderTime by remember { mutableStateOf(todo.reminderTime) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // 삭제 확인 다이얼로그
    if (showDeleteConfirm) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                onDelete()
                onDismiss()
            }
        )
    }

    // 날짜 선택 다이얼로그
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 시간 선택 다이얼로그
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = if (reminderTime != null) {
                Calendar.getInstance().apply { timeInMillis = reminderTime!! }.get(Calendar.HOUR_OF_DAY)
            } else {
                9
            },
            initialMinute = if (reminderTime != null) {
                Calendar.getInstance().apply { timeInMillis = reminderTime!! }.get(Calendar.MINUTE)
            } else {
                0
            }
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        if (dueDate != null) {
                            calendar.timeInMillis = dueDate!!
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        calendar.set(Calendar.SECOND, 0)
                        reminderTime = calendar.timeInMillis
                        showTimePicker = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("취소")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
    
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

                // 마감일 선택
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (dueDate != null) {
                            SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN)
                                .format(Date(dueDate!!))
                        } else {
                            "마감일 설정"
                        }
                    )
                    if (dueDate != null) {
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { dueDate = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, "제거", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // 알림 시간 선택
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Notifications, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (reminderTime != null) {
                            SimpleDateFormat("HH:mm 알림", Locale.KOREAN)
                                .format(Date(reminderTime!!))
                        } else {
                            "알림 설정"
                        }
                    )
                    if (reminderTime != null) {
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { reminderTime = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, "제거", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Divider()

                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 삭제 버튼
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
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
                                description = description.ifBlank { null },
                                dueDate = dueDate,
                                reminderTime = reminderTime
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
