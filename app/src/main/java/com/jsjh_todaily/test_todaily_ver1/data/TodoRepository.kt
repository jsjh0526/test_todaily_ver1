package com.jsjh_todaily.test_todaily_ver1.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    // 전체 할일
    val allTodos: Flow<List<Todo>> = todoDao.getAllTodos()

    // 완료되지 않은 할일
    val incompleteTodos: Flow<List<Todo>> = todoDao.getIncompleteTodos()

    // 우선순위별 조회
    fun getTodosByPriority(priority: Priority): Flow<List<Todo>> {
        return todoDao.getTodosByPriority(priority.ordinal)
    }

    // 태그 검색
    fun searchByTag(tag: String): Flow<List<Todo>> {
        return todoDao.searchByTag(tag)
    }

    // 검색
    fun searchTodos(query: String): Flow<List<Todo>> {
        return todoDao.searchTodos(query)
    }

    // ID로 조회
    suspend fun getTodoById(id: Int): Todo? {
        return todoDao.getTodoById(id)
    }

    // 삽입
    suspend fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    // 업데이트
    suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    // 삭제
    suspend fun delete(todo: Todo) {
        todoDao.delete(todo)
    }

    // 완료 토글
    suspend fun toggleComplete(todo: Todo) {
        todoDao.updateCompleted(todo.id, !todo.isCompleted)
    }

    // 전체 삭제
    suspend fun deleteAll() {
        todoDao.deleteAll()
    }
}
