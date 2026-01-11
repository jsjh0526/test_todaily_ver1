package com.example.test_todaily_ver1.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // 전체 조회 (최신순)
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<Todo>>

    // 우선순위별 조회
    @Query("SELECT * FROM todos WHERE priority = :priorityOrdinal ORDER BY createdAt DESC")
    fun getTodosByPriority(priorityOrdinal: Int): Flow<List<Todo>>

    // 완료되지 않은 할일만 조회
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getIncompleteTodos(): Flow<List<Todo>>

    // 태그 검색
    @Query("SELECT * FROM todos WHERE tags LIKE '%' || :tag || '%' ORDER BY createdAt DESC")
    fun searchByTag(tag: String): Flow<List<Todo>>

    // 검색 (제목)
    @Query("SELECT * FROM todos WHERE content LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<Todo>>

    // ID로 조회
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Int): Todo?

    // 삽입
    @Insert
    suspend fun insert(todo: Todo)

    // 업데이트
    @Update
    suspend fun update(todo: Todo)

    // 삭제
    @Delete
    suspend fun delete(todo: Todo)

    // 완료 상태 토글
    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompleted(id: Int, isCompleted: Boolean)

    // 전체 삭제
    @Query("DELETE FROM todos")
    suspend fun deleteAll()
}
