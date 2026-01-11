package com.example.test_todaily_ver1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_todaily_ver1.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoRepository

    // 전체 할일
    val allTodos: StateFlow<List<Todo>>

    // 우선순위 높은 할일
    val highPriorityTodos: StateFlow<List<Todo>>

    // 검색/필터 상태
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showCompleted = MutableStateFlow(false)
    val showCompleted: StateFlow<Boolean> = _showCompleted

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    // 필터링된 할일 목록
    val filteredTodos: StateFlow<List<Todo>>

    init {
        val todoDao = TodoDatabase.getDatabase(application).todoDao()
        repository = TodoRepository(todoDao)

        // 전체 할일
        allTodos = repository.allTodos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // 우선순위 높은 할일
        highPriorityTodos = repository.getTodosByPriority(Priority.HIGH).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // 필터링/정렬된 할일
        filteredTodos = combine(
            allTodos,
            searchQuery,
            showCompleted,
            sortOrder
        ) { todos, query, showComp, sort ->
            var result = todos

            // 완료된 할일 필터링
            if (!showComp) {
                result = result.filter { !it.isCompleted }
            }

            // 검색어 필터링
            if (query.isNotEmpty()) {
                result = if (query.startsWith("#")) {
                    // 태그 검색
                    val tag = query.drop(1)
                    result.filter { it.tags.any { t -> t.contains(tag, ignoreCase = true) } }
                } else {
                    // 일반 검색
                    result.filter { it.content.contains(query, ignoreCase = true) }
                }
            }

            // 정렬
            when (sort) {
                SortOrder.NEWEST -> result.sortedByDescending { it.createdAt }
                SortOrder.PRIORITY -> result.sortedByDescending { it.priority.ordinal }
                SortOrder.STATUS -> result.sortedBy { it.isCompleted }
                SortOrder.DUE_DATE -> result.sortedBy { it.dueDate ?: Long.MAX_VALUE }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // 할일 추가
    fun addTodo(
        content: String,
        priority: Priority,
        tags: List<String> = emptyList(),
        description: String? = null,
        dueDate: Long? = null,
        reminderTime: Long? = null
    ) {
        viewModelScope.launch {
            repository.insert(
                Todo(
                    content = content,
                    priority = priority,
                    tags = tags,
                    description = description,
                    dueDate = dueDate,
                    reminderTime = reminderTime
                )
            )
        }
    }

    // 할일 수정
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            repository.update(todo)
        }
    }

    // 할일 삭제
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }

    // 완료 토글
    fun toggleComplete(todo: Todo) {
        viewModelScope.launch {
            repository.toggleComplete(todo)
        }
    }

    // 검색어 업데이트
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 완료 표시 토글
    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    // 정렬 순서 변경
    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }
}

enum class SortOrder {
    NEWEST,    // 최신순
    PRIORITY,  // 우선도순
    STATUS,    // 상태순
    DUE_DATE   // 마감일순
}
