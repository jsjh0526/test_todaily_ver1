package com.jsjh_todaily.test_todaily_ver1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jsjh_todaily.test_todaily_ver1.data.*
import com.jsjh_todaily.test_todaily_ver1.data.export.DataManager
import com.jsjh_todaily.test_todaily_ver1.notification.AlarmScheduler
import java.io.File
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoRepository
    private val alarmScheduler = AlarmScheduler(application)
    private val dataManager = DataManager(application)

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
                SortOrder.PRIORITY -> result.sortedBy { it.priority.ordinal }  // 높음(0) → 낮음(2)
                SortOrder.TITLE -> result.sortedBy { it.content }  // 가나다순
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
        reminderTimes: List<Long> = emptyList()
    ) {
        viewModelScope.launch {
            val todo = Todo(
                content = content,
                priority = priority,
                tags = tags,
                description = description,
                dueDate = dueDate,
                reminderTimes = reminderTimes
            )
            repository.insert(todo)
            
            // 알림 예약
            alarmScheduler.scheduleAlarm(todo)
        }
    }

    // 할일 수정
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            // 기존 알림 취소
            alarmScheduler.cancelAlarm(todo)
            
            // DB 업데이트
            repository.update(todo)
            
            // 새로운 알림 예약
            alarmScheduler.scheduleAlarm(todo)
        }
    }

    // 할일 삭제
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            // 알림 취소
            alarmScheduler.cancelAlarm(todo)
            
            // DB 삭제
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

    // ========== 데이터 백업/복구 ==========
    
    /**
     * 데이터 내보내기 (JSON)
     * @return 저장된 파일 경로 또는 null
     */
    suspend fun exportData(): File? {
        return try {
            val todos = allTodos.value
            dataManager.exportToJson(todos)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 데이터 불러오기 (JSON 파일)
     * @param file 불러올 파일
     * @return 성공 여부
     */
    suspend fun importData(file: File): Boolean {
        return try {
            val todos = dataManager.importFromJson(file) ?: return false
            
            // 기존 데이터 삭제
            repository.deleteAll()
            
            // 새로운 데이터 추가
            todos.forEach { todo ->
                repository.insert(todo)
                // 알림 재설정
                alarmScheduler.scheduleAlarm(todo)
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 데이터 불러오기 (URI)
     * @param uriString 파일 URI
     * @return 성공 여부
     */
    suspend fun importDataFromUri(uriString: String): Boolean {
        return try {
            val todos = dataManager.importFromUri(uriString) ?: return false
            
            // 기존 데이터 삭제
            repository.deleteAll()
            
            // 새로운 데이터 추가
            todos.forEach { todo ->
                repository.insert(todo)
                // 알림 재설정
                alarmScheduler.scheduleAlarm(todo)
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

enum class SortOrder {
    NEWEST,    // 최신순
    PRIORITY,  // 우선도순 (높음→낮음)
    TITLE,     // 제목순 (가나다순)
    DUE_DATE   // 마감일순
}
