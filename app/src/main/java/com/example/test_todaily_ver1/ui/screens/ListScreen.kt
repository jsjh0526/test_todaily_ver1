package com.example.test_todaily_ver1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.test_todaily_ver1.viewmodel.SortOrder
import com.example.test_todaily_ver1.viewmodel.TodoViewModel
import com.example.test_todaily_ver1.ui.components.TodoItem
import com.example.test_todaily_ver1.ui.dialogs.TodoDetailDialog
import com.example.test_todaily_ver1.ui.dialogs.DeleteConfirmDialog

@Composable
fun ListScreen(viewModel: TodoViewModel) {
    val isDarkTheme = isSystemInDarkTheme()
    val filteredTodos by viewModel.filteredTodos.collectAsState()
    val allTodos by viewModel.allTodos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showCompleted by viewModel.showCompleted.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<com.example.test_todaily_ver1.data.Todo?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<com.example.test_todaily_ver1.data.Todo?>(null) }
    
    val existingTags = remember(allTodos) {
        allTodos.flatMap { it.tags }.distinct().take(5)
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "할일 목록",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF312C85)
                )
                Text(
                    "모든 할일을 관리하세요",
                    fontSize = 16.sp,
                    color = if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFF4A5565)
                )
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 검색
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { 
                            Text(
                                "할 일 검색... (#태그명)",
                                color = Color(0xFF99A1AF)
                            ) 
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Search,
                                "검색",
                                tint = Color(0xFF717182)
                            ) 
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Close, "지우기")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = if (isDarkTheme) Color(0xFF3A3A3A) else Color(0xFFF3F3F5),
                            focusedContainerColor = if (isDarkTheme) Color(0xFF3A3A3A) else Color(0xFFF3F3F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFF615FFF),
                            unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0A0A0A),
                            focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0A0A0A)
                        ),
                        singleLine = true,
                        maxLines = 1
                    )

                    // 태그 필터
                    if (existingTags.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(existingTags) { tag ->
                                FilterChip(
                                    selected = searchQuery == "#$tag",
                                    onClick = { 
                                        viewModel.updateSearchQuery(
                                            if (searchQuery == "#$tag") "" else "#$tag"
                                        )
                                    },
                                    label = { Text("#$tag") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF615FFF),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // 필터/정렬
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = showCompleted,
                            onClick = { viewModel.toggleShowCompleted() },
                            label = { Text("완료 보기") },
                            leadingIcon = {
                                Icon(
                                    if (showCompleted) Icons.Default.CheckBox 
                                    else Icons.Default.CheckBoxOutlineBlank,
                                    null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4F39F6).copy(alpha = 0.1f),
                                selectedLabelColor = Color(0xFF4F39F6)
                            )
                        )

                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Default.Sort, "정렬")
                            }
                            
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("최신순") },
                                    onClick = {
                                        viewModel.updateSortOrder(SortOrder.NEWEST)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOrder == SortOrder.NEWEST) {
                                            Icon(Icons.Default.Check, null)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("우선도순") },
                                    onClick = {
                                        viewModel.updateSortOrder(SortOrder.PRIORITY)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOrder == SortOrder.PRIORITY) {
                                            Icon(Icons.Default.Check, null)
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("상태순") },
                                    onClick = {
                                        viewModel.updateSortOrder(SortOrder.STATUS)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOrder == SortOrder.STATUS) {
                                            Icon(Icons.Default.Check, null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 목록
            if (filteredTodos.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Assignment,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF99A1AF).copy(0.3f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "표시할 할일이 없습니다.",
                            fontSize = 16.sp,
                            color = Color(0xFF99A1AF)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTodos, key = { it.id }) { todo ->
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
            }
        }
    }
}
