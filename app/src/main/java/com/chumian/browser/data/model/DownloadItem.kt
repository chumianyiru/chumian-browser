package com.chumian.browser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val fileName: String,
    val filePath: String,
    val mimeType: String,
    val totalSize: Long = 0,
    val downloadedSize: Long = 0,
    val status: Int = STATUS_PENDING,
    val speed: Long = 0,
    val addedTime: Long = System.currentTimeMillis(),
    val completedTime: Long = 0,
    val errorMessage: String? = null,
    val isResumable: Boolean = true
) {
    companion object {
        const val STATUS_PENDING = 0
        const val STATUS_DOWNLOADING = 1
        const val STATUS_PAUSED = 2
        const val STATUS_COMPLETED = 3
        const val STATUS_FAILED = 4
        const val STATUS_CANCELLED = 5
    }
}
