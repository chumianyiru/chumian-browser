package com.chumian.browser.data.model

data class DownloadItem(
    val id: Long = System.currentTimeMillis(),
    val url: String,
    val fileName: String,
    val mimeType: String?,
    val contentLength: Long = 0,
    val downloadedBytes: Long = 0,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val filePath: String? = null,
    val createTime: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}
