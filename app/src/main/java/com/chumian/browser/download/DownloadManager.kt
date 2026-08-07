package com.chumian.browser.download

import android.app.DownloadManager as SystemDownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.chumian.browser.ChumianApp
import com.chumian.browser.data.model.DownloadItem
import com.chumian.browser.data.dao.DownloadDao
import kotlinx.coroutines.flow.Flow

class DownloadManager(private val context: Context) {
    private val downloadDao: DownloadDao = (context as ChumianApp).database.downloadDao()
    private val systemDownloadManager: SystemDownloadManager = 
        context.getSystemService(Context.DOWNLOAD_SERVICE) as SystemDownloadManager
    
    fun getAllDownloads(): Flow<List<DownloadItem>> {
        return downloadDao.getAllDownloads()
    }
    
    fun getDownloadsByStatus(status: Int): Flow<List<DownloadItem>> {
        return downloadDao.getDownloadsByStatus(status)
    }
    
    fun searchDownloads(query: String): Flow<List<DownloadItem>> {
        return downloadDao.searchDownloads("%$query%")
    }
    
    suspend fun startDownload(url: String, fileName: String, mimeType: String): Long {
        val request = SystemDownloadManager.Request(Uri.parse(url)).apply {
            setTitle(fileName)
            setDescription("正在下载...")
            setMimeType(mimeType)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "ChumianBrowser/$fileName"
            )
            setNotificationVisibility(SystemDownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        
        val downloadId = systemDownloadManager.enqueue(request)
        
        val downloadItem = DownloadItem(
            id = downloadId,
            url = url,
            fileName = fileName,
            filePath = "${Environment.DIRECTORY_DOWNLOADS}/ChumianBrowser/$fileName",
            mimeType = mimeType,
            status = DownloadItem.STATUS_DOWNLOADING,
            addedTime = System.currentTimeMillis()
        )
        
        downloadDao.insertDownload(downloadItem)
        return downloadId
    }
    
    suspend fun pauseDownload(id: Long) {
        // Android系统DownloadManager不直接支持暂停，这里只是更新状态
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            downloadDao.updateDownload(download.copy(status = DownloadItem.STATUS_PAUSED))
        }
    }
    
    suspend fun resumeDownload(id: Long) {
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            downloadDao.updateDownload(download.copy(status = DownloadItem.STATUS_DOWNLOADING))
        }
    }
    
    suspend fun cancelDownload(id: Long) {
        systemDownloadManager.remove(id)
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            downloadDao.updateDownload(download.copy(status = DownloadItem.STATUS_CANCELLED))
        }
    }
    
    suspend fun deleteDownload(id: Long, deleteFile: Boolean = false) {
        if (deleteFile) {
            systemDownloadManager.remove(id)
        }
        downloadDao.deleteDownloadById(id)
    }
    
    suspend fun deleteAllDownloads(deleteFiles: Boolean = false) {
        if (deleteFiles) {
            val downloads = downloadDao.getAllDownloads()
            // 注意：这里需要在协程中收集
        }
        downloadDao.deleteAllDownloads()
    }
    
    suspend fun getDownloadById(id: Long): DownloadItem? {
        return downloadDao.getDownloadById(id)
    }
    
    suspend fun getDownloadByUrl(url: String): DownloadItem? {
        return downloadDao.getDownloadByUrl(url)
    }
    
    suspend fun getDownloadCountByStatus(status: Int): Int {
        return downloadDao.getDownloadCountByStatus(status)
    }
    
    fun getDownloadProgress(id: Long): Int {
        val query = SystemDownloadManager.Query().setFilterById(id)
        val cursor = systemDownloadManager.query(query)
        return if (cursor.moveToFirst()) {
            val bytesDownloaded = cursor.getInt(
                cursor.getColumnIndexOrThrow(SystemDownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            )
            val bytesTotal = cursor.getInt(
                cursor.getColumnIndexOrThrow(SystemDownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            )
            if (bytesTotal > 0) {
                (bytesDownloaded * 100 / bytesTotal)
            } else {
                0
            }
        } else {
            0
        }.also {
            cursor.close()
        }
    }
    
    fun getDownloadStatus(id: Long): Int {
        val query = SystemDownloadManager.Query().setFilterById(id)
        val cursor = systemDownloadManager.query(query)
        return if (cursor.moveToFirst()) {
            val status = cursor.getInt(
                cursor.getColumnIndexOrThrow(SystemDownloadManager.COLUMN_STATUS)
            )
            when (status) {
                SystemDownloadManager.STATUS_SUCCESSFUL -> DownloadItem.STATUS_COMPLETED
                SystemDownloadManager.STATUS_FAILED -> DownloadItem.STATUS_FAILED
                SystemDownloadManager.STATUS_PAUSED -> DownloadItem.STATUS_PAUSED
                SystemDownloadManager.STATUS_PENDING -> DownloadItem.STATUS_PENDING
                SystemDownloadManager.STATUS_RUNNING -> DownloadItem.STATUS_DOWNLOADING
                else -> DownloadItem.STATUS_PENDING
            }
        } else {
            DownloadItem.STATUS_FAILED
        }.also {
            cursor.close()
        }
    }
    
    fun openDownloadedFile(id: Long): Uri? {
        return try {
            systemDownloadManager.getUriForDownloadedFile(id)
        } catch (e: Exception) {
            null
        }
    }
}
