package com.example.test_todaily_ver1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.test_todaily_ver1.data.Priority
import com.example.test_todaily_ver1.viewmodel.TodoViewModel
import com.example.test_todaily_ver1.ui.components.TodoItem
import com.example.test_todaily_ver1.ui.components.PriorityButton
import com.example.test_todaily_ver1.ui.components.TagChip
import com.example.test_todaily_ver1.ui.dialogs.TodoDetailDialog
import com.example.test_todaily_ver1.ui.dialogs.DeleteConfirmDialog

@Composable
fun HomeScreen(viewModel: TodoViewModel) {
    val isDarkTheme = isSystemInDarkTheme()
    val highPriorityTodos by viewModel.highPriorityTodos.collectAsState()
    
    var todoInput by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var tagInput by remember { mutableStateOf("") }
    
    var selectedTodo by remember { mutableStateOf<com.example.test_todaily_ver1.data.Todo?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<com.example.test_todaily_ver1.data.Todo?>(null) }
    
    val quotes = listOf(
        "작은 발걸음도 앞으로 나아가는 것입니다.",
        "오늘 할 일을 내일로 미루지 마세요.",
        "계획은 꿈을 현실로 만듭니다.",
        "성공은 매일의 작은 노력의 합입니다.",
        "시작이 반입니다."
    )
    var currentQuote by remember { mutableStateOf(quotes.random()) }

    if (showDialog && selectedTodo != null) {
        TodoDetailDialog(
            todo = selectedTodo!!,
            onDismiss = { showDialog = false },
            onSave = { viewModel.updateTodo(it) },
            onDelete = { viewModel.deleteTodo(selectedTodo!!) }
        )
    }

    if (showDeleteDialog && todoToDelete != null) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteTodo(todoToDelete!!)
                todoToDelete = null
            }
        )
    }

    // MainActivity 배경 사용 (투명하게)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 명언 카드 (보라 그라데이션)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF615FFF),  // 보라
                                        Color(0xFF9810FA)   // 진보라
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "\" $currentQuote \"",
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                            IconButton(onClick = { currentQuote = quotes.random() }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    "새로고침",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 헤더
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = Color(0xFF615FFF),  // 피그마 보라
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "할 일 관리",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF312C85)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "오늘의 할 일을 관리해보세요",
                        fontSize = 16.sp,
                        color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF4A5565)
                    )
                }
            }

            // 할일 추가 카드
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(25.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "새로운 할 일 추가",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) Color.White else Color(0xFF0A0A0A)
                        )
                        
                        Text(
                            "해야 할 일을 입력하고 Enter를 누르거나 추가 버튼을 클릭하세요",
                            fontSize = 16.sp,
                            color = Color(0xFF717182),  // 피그마 설명
                            lineHeight = 24.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically  // 중간 정렬!
                        ) {
                            OutlinedTextField(
                                value = todoInput,
                                onValueChange = { todoInput = it },
                                placeholder = { 
                                    Text(
                                        "할 일을 입력하세요...",
                                        color = Color(0xFF717182)
                                    ) 
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp),  // 조금 크게!
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White,  // 항상 흰색!
                                    focusedContainerColor = Color.White,    // 항상 흰색!
                                    disabledContainerColor = Color.White,   // 비활성화시에도 흰색
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color(0xFF615FFF),
                                    disabledBorderColor = Color.Transparent,
                                    unfocusedTextColor = Color(0xFF0A0A0A),  // 항상 검정!
                                    focusedTextColor = Color(0xFF0A0A0A),    // 항상 검정!
                                    disabledTextColor = Color(0xFF0A0A0A),   // 비활성화시에도 검정
                                    cursorColor = Color(0xFF615FFF),         // 커서 보라색
                                    unfocusedPlaceholderColor = Color(0xFF717182),
                                    focusedPlaceholderColor = Color(0xFF717182)
                                ),
                                singleLine = true,
                                maxLines = 1
                            )
                            
                            // 피그마 추가 버튼 (검정)
                            Button(
                                onClick = {
                                    if (todoInput.isNotBlank()) {
                                        viewModel.addTodo(
                                            content = todoInput,
                                            priority = selectedPriority,
                                            tags = tags
                                        )
                                        todoInput = ""
                                        tags = emptyList()
                                        selectedPriority = Priority.MEDIUM
                                    }
                                },
                                enabled = todoInput.isNotBlank(),
                                modifier = Modifier
                                    .width(100.dp)  // 조금 작게
                                    .height(52.dp),  // 조금 작게
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF030213),  // 피그마 검정
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("추가", fontSize = 16.sp)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "우선도:",
                                fontSize = 14.sp,
                                color = Color(0xFF717182)
                            )
                            PriorityButton(Priority.HIGH, selectedPriority == Priority.HIGH) { 
                                selectedPriority = Priority.HIGH 
                            }
                            PriorityButton(Priority.MEDIUM, selectedPriority == Priority.MEDIUM) { 
                                selectedPriority = Priority.MEDIUM 
                            }
                            PriorityButton(Priority.LOW, selectedPriority == Priority.LOW) { 
                                selectedPriority = Priority.LOW 
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (tags.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    tags.forEach { tag ->
                                        TagChip(tag) { tags = tags - tag }
                                    }
                                }
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "태그:",
                                    fontSize = 14.sp,
                                    color = Color(0xFF717182)
                                )
                                OutlinedTextField(
                                    value = tagInput,
                                    onValueChange = { tagInput = it },
                                    placeholder = { 
                                        Text(
                                            "태그 입력 후 Enter...",
                                            fontSize = 14.sp,
                                            color = Color(0xFF717182)
                                        ) 
                                    },
                                    modifier = Modifier.weight(1f),  // 원래 크기로 롤백!
                                    shape = RoundedCornerShape(4.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.White,  // 항상 흰색!
                                        focusedContainerColor = Color.White,    // 항상 흰색!
                                        disabledContainerColor = Color.White,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = Color(0xFF615FFF),
                                        disabledBorderColor = Color.Transparent,
                                        unfocusedTextColor = Color(0xFF0A0A0A),  // 항상 검정!
                                        focusedTextColor = Color(0xFF0A0A0A),    // 항상 검정!
                                        disabledTextColor = Color(0xFF0A0A0A),
                                        cursorColor = Color(0xFF615FFF),
                                        unfocusedPlaceholderColor = Color(0xFF717182),
                                        focusedPlaceholderColor = Color(0xFF717182)
                                    ),
                                    singleLine = true,
                                    maxLines = 1,
                                    trailingIcon = {
                                        if (tagInput.isNotBlank()) {
                                            IconButton(onClick = {
                                                val newTag = tagInput.trim()
                                                if (newTag.isNotBlank() && !tags.contains(newTag)) {
                                                    tags = tags + newTag
                                                    tagInput = ""
                                                }
                                            }) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    "추가",
                                                    tint = Color(0xFF615FFF)
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
                    }
                }
            }

            // 우선순위 높은 할일
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "우선순위 높은 할 일",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0A0A0A)
                    )
                    Text(
                        "${highPriorityTodos.count { !it.isCompleted }}개의 중요한 할 일",
                        fontSize = 16.sp,
                        color = Color(0xFF717182)
                    )
                }
            }

            if (highPriorityTodos.filter { !it.isCompleted }.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF99A1AF).copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "우선순위가 높은 할 일이 없습니다.",
                                fontSize = 16.sp,
                                color = Color(0xFF99A1AF)
                            )
                        }
                    }
                }
            } else {
                items(highPriorityTodos.filter { !it.isCompleted }) { todo ->
                    TodoItem(
                        todo,
                        { viewModel.toggleComplete(todo) },
                        {
                            todoToDelete = todo
                            showDeleteDialog = true
                        },
                        {
                            selectedTodo = todo
                            showDialog = true
                        }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
