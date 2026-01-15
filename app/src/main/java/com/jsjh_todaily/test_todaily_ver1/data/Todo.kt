package com.jsjh_todaily.test_todaily_ver1.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "todos")
@TypeConverters(Converters::class)
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,                      // 할일 내용
    val description: String? = null,          // 상세 설명
    val priority: Priority,                   // 우선순위
    val tags: List<String> = emptyList(),     // 태그
    val dueDate: Long? = null,                // 마감일 (날짜+시간 timestamp)
    val reminderTimes: List<Long> = emptyList(), // 알림 시간들 (여러 개 가능)
    val isCompleted: Boolean = false,         // 완료 상태
    val createdAt: Long = System.currentTimeMillis() // 생성 시간
)

// Room TypeConverter
class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.ordinal
    }

    @TypeConverter
    fun toPriority(ordinal: Int): Priority {
        return Priority.entries[ordinal]
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }

    @TypeConverter
    fun fromLongList(list: List<Long>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toLongList(data: String): List<Long> {
        return if (data.isEmpty()) emptyList() else data.split(",").map { it.toLong() }
    }
}
