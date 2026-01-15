package com.jsjh_todaily.test_todaily_ver1.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jsjh_todaily.test_todaily_ver1.data.Todo

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 알림 예약
    fun scheduleAlarm(todo: Todo) {
        // 마감일 알림
        todo.dueDate?.let { dueDate ->
            scheduleNotification(
                todo.id,
                dueDate,
                todo.content,
                "마감일이 도래했습니다!"
            )
        }

        // 커스텀 알림들
        todo.reminderTimes.forEachIndexed { index, reminderTime ->
            scheduleNotification(
                todo.id * 1000 + index, // 고유 ID
                reminderTime,
                todo.content,
                "설정한 알림 시간입니다!"
            )
        }
    }

    private fun scheduleNotification(
        requestCode: Int,
        triggerTime: Long,
        title: String,
        message: String
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TODO_ID", requestCode)
            putExtra("TODO_TITLE", title)
            putExtra("TODO_MESSAGE", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 과거 시간이면 알림 설정 안함
        if (triggerTime > System.currentTimeMillis()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Android 12+ 정확한 알람 권한 필요
                e.printStackTrace()
            }
        }
    }

    // 알림 취소
    fun cancelAlarm(todo: Todo) {
        // 마감일 알림 취소
        cancelNotification(todo.id)

        // 커스텀 알림들 취소
        todo.reminderTimes.forEachIndexed { index, _ ->
            cancelNotification(todo.id * 1000 + index)
        }
    }

    private fun cancelNotification(requestCode: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // 모든 알림 취소
    fun cancelAllAlarms(todos: List<Todo>) {
        todos.forEach { cancelAlarm(it) }
    }
}
