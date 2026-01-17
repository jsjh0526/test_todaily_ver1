package com.jsjh_todaily.test_todaily_ver1.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.jsjh_todaily.test_todaily_ver1.data.Priority
import com.jsjh_todaily.test_todaily_ver1.data.Todo
import com.jsjh_todaily.test_todaily_ver1.ui.components.PriorityButton
import com.jsjh_todaily.test_todaily_ver1.ui.components.TagChip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var reminderTimes by remember { mutableStateOf(todo.reminderTimes) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderDatePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    var tempReminderDate by remember { mutableStateOf<Long?>(null) }

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

    // 마감일 날짜 선택
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // 오늘 이후만 선택 가능 (오늘 00:00:00 기준)
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    return utcTimeMillis >= today
                }
            }
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                        showTimePicker = true
                    }
                ) {
                    Text("다음")
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

    // 마감일 시간 선택
    if (showTimePicker && dueDate != null) {
        val calendar = Calendar.getInstance().apply { timeInMillis = dueDate!! }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCalendar = Calendar.getInstance().apply {
                            timeInMillis = dueDate!!
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                        }
                        dueDate = newCalendar.timeInMillis
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("시간 설정", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    // 알림 날짜 선택
    if (showReminderDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // 오늘 이후만 선택 가능 (오늘 00:00:00 기준)
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    return utcTimeMillis >= today
                }
            }
        )
        
        DatePickerDialog(
            onDismissRequest = { showReminderDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        tempReminderDate = datePickerState.selectedDateMillis
                        showReminderDatePicker = false
                        showReminderTimePicker = true
                    }
                ) {
                    Text("다음")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReminderDatePicker = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 알림 시간 선택
    if (showReminderTimePicker && tempReminderDate != null) {
        val timePickerState = rememberTimePickerState(
            initialHour = 9,
            initialMinute = 0
        )
        
        AlertDialog(
            onDismissRequest = { 
                showReminderTimePicker = false
                tempReminderDate = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = tempReminderDate!!
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                        }
                        reminderTimes = reminderTimes + calendar.timeInMillis
                        showReminderTimePicker = false
                        tempReminderDate = null
                    }
                ) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showReminderTimePicker = false
                    tempReminderDate = null
                }) {
                    Text("취소")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("알림 시간 설정", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f),  // 화면의 95% 높이로 확대
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 고정 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
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

                // 스크롤 가능한 콘텐츠
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 할일 내용
                    item {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("할 일") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // 우선순위
                    item {
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
                    }

                    // 태그
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "태그",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            if (tags.isNotEmpty()) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    }

                    // 메모
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("메모") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 4
                        )
                    }

                    // 마감일 설정
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "마감일",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (dueDate != null) {
                                        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREAN)
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
                        }
                    }

                    // 알림 설정 (여러 개)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "알림",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            // 기존 알림들 표시
                            if (reminderTimes.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    reminderTimes.sortedDescending().forEach { reminderTime ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.NotificationsActive,
                                                        null,
                                                        modifier = Modifier.size(18.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREAN)
                                                            .format(Date(reminderTime)),
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { reminderTimes = reminderTimes - reminderTime },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        "제거",
                                                        modifier = Modifier.size(18.dp),
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // 알림 추가 버튼
                            OutlinedButton(
                                onClick = { showReminderDatePicker = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("알림 추가")
                            }
                        }
                    }

                    // 여백
                    item {
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Divider()

                // 고정 버튼들
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
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
                                reminderTimes = reminderTimes
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
