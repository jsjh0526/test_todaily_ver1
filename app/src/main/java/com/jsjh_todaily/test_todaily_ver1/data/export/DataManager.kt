package com.jsjh_todaily.test_todaily_ver1.data.export

import android.content.Context
import android.os.Environment
import com.jsjh_todaily.test_todaily_ver1.data.Todo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class DataManager(private val context: Context) {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    /**
     * 할일 목록을 JSON 파일로 내보내기
     * @param todos 내보낼 할일 목록
     * @return 저장된 파일 경로 (성공시) 또는 null (실패시)
     */
    fun exportToJson(todos: List<Todo>): File? {
        try {
            // Downloads 폴더에 저장
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            // 파일 이름: todaily_backup_YYYYMMDD_HHMMSS.json
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "todaily_backup_$timestamp.json"
            val file = File(downloadsDir, fileName)

            // JSON 변환 및 저장
            val backupData = BackupData(
                version = 1,
                exportDate = System.currentTimeMillis(),
                todos = todos
            )

            FileWriter(file).use { writer ->
                gson.toJson(backupData, writer)
            }

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * JSON 파일에서 할일 목록 불러오기
     * @param file 불러올 JSON 파일
     * @return 할일 목록 (성공시) 또는 null (실패시)
     */
    fun importFromJson(file: File): List<Todo>? {
        try {
            if (!file.exists() || !file.canRead()) {
                return null
            }

            FileReader(file).use { reader ->
                val backupData = gson.fromJson(reader, BackupData::class.java)
                return backupData?.todos
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * URI에서 할일 목록 불러오기 (파일 선택기에서)
     * @param uriString 파일 URI
     * @return 할일 목록 (성공시) 또는 null (실패시)
     */
    fun importFromUri(uriString: String): List<Todo>? {
        try {
            val uri = android.net.Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                val backupData = gson.fromJson(json, BackupData::class.java)
                return backupData?.todos
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

/**
 * 백업 데이터 구조
 */
data class BackupData(
    val version: Int,           // 백업 파일 버전
    val exportDate: Long,       // 내보내기 날짜
    val todos: List<Todo>       // 할일 목록
)
