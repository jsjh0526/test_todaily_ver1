package com.example.test_todaily_ver1.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test_todaily_ver1.data.Priority
import com.example.test_todaily_ver1.viewmodel.TodoViewModel
import com.example.test_todaily_ver1.ui.components.TodoItem
import com.example.test_todaily_ver1.ui.components.PriorityButton
import com.example.test_todaily_ver1.ui.components.TagChip
import com.example.test_todaily_ver1.ui.dialogs.TodoDetailDialog

@Composable
fun HomeScreen(viewModel: TodoViewModel) {
    val highPriorityTodos by viewModel.highPriorityTodos.collectAsState()
    
    var todoInput by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var tagInput by remember { mutableStateOf("") }
    
    var selectedTodo by remember { mutableStateOf<com.example.test_todaily_ver1.data.Todo?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
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

    // MaterialTheme 색상만 사용 (자동 전환!)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 명언 카드
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "\" $currentQuote \"",
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 24.sp
                        )
                        IconButton(onClick = { currentQuote = quotes.random() }) {
                            Icon(
                                Icons.Default.Refresh,
                                "새로고침",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
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
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "할 일 관리",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "오늘의 할 일을 관리해보세요",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 할일 추가 카드
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            "해야 할 일을 입력하고 Enter를 누르거나 추가 버튼을 클릭하세요",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 24.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = todoInput,
                                onValueChange = { todoInput = it },
                                placeholder = { Text("할 일을 입력하세요...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            
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
                                modifier = Modifier.width(104.dp),
                                shape = RoundedCornerShape(10.dp)
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
                            Text("우선도:", fontSize = 14.sp)
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
                                Text("태그:", fontSize = 14.sp)
                                OutlinedTextField(
                                    value = tagInput,
                                    onValueChange = { tagInput = it },
                                    placeholder = { Text("태그 입력 후 Enter...") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    singleLine = true,
                                    trailingIcon = {
                                        if (tagInput.isNotBlank()) {
                                            IconButton(onClick = {
                                                val newTag = tagInput.trim()
                                                if (newTag.isNotBlank() && !tags.contains(newTag)) {
                                                    tags = tags + newTag
                                                    tagInput = ""
                                                }
                                            }) {
                                                Icon(Icons.Default.Add, "추가")
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
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${highPriorityTodos.count { !it.isCompleted }}개의 중요한 할 일",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
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
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "우선순위가 높은 할 일이 없습니다.",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(highPriorityTodos.filter { !it.isCompleted }) { todo ->
                    TodoItem(
                        todo,
                        { viewModel.toggleComplete(todo) },
                        { viewModel.deleteTodo(todo) },
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
