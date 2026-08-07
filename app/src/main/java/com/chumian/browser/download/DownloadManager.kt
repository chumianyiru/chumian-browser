package com.chumian.browser.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import com.chumian.browser.data.model.DownloadItem
import com.chumian.browser.data.model.DownloadStatus

class DownloadManager(context: Context) {
    private val appContext = context.applicationContext
    private val downloadManager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun startDownload(url: String, userAgent: String? = null): Long {
        val request = DownloadManager.Request(Uri.parse(url))

        // 设置文件名
        val fileName = URLUtil.guessFileName(url, null, null)
        request.setTitle(fileName)
        request.setDescription("下载中...")

        // 设置下载目录
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        // 设置通知可见性
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        // 设置允许的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

        // 设置Cookie
        val cookie = CookieManager.getInstance().getCookie(url)
        if (cookie != null) {
            request.addRequestHeader("Cookie", cookie)
        }

        // 设置User-Agent
        if (userAgent != null) {
            request.addRequestHeader("User-Agent", userAgent)
        }

        // 允许扫描
        request.allowScanningByMediaScanner()

        return downloadManager.enqueue(request)
    }

    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        return if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            when (cursor.getInt(statusIndex)) {
                DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
                DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                DownloadManager.STATUS_PAUSED -> DownloadStatus.PAUSED
                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                else -> DownloadStatus.FAILED
            }
        } else {
            DownloadStatus.FAILED
        }.also {
            cursor.close()
        }
    }

    fun getDownloadProgress(downloadId: Long): Pair<Long, Long> {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        return if (cursor.moveToFirst()) {
            val bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val bytesDownloaded: Long = cursor.getLong(bytesDownloadedIndex)
            val bytesTotal: Long = cursor.getLong(bytesTotalIndex)
            Pair(bytesDownloaded, bytesTotal)
        } else {
            Pair(0L, 0L)
        }.also {
            cursor.close()
        }
    }

    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }

    fun getDownloadUri(downloadId: Long): Uri? {
        return downloadManager.getUriForDownloadedFile(downloadId)
    }
}
