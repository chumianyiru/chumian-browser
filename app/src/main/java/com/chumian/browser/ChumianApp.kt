package com.chumian.browser

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.chumian.browser.bookmark.BookmarkManager
import com.chumian.browser.history.HistoryManager

class ChumianApp : Application() {
    lateinit var bookmarkManager: BookmarkManager
        private set

    lateinit var historyManager: HistoryManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        bookmarkManager = BookmarkManager(this)
        historyManager = HistoryManager(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val downloadChannel = NotificationChannel(
                CHANNEL_DOWNLOAD,
                "下载通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "下载进度通知"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(downloadChannel)
        }
    }

    companion object {
        lateinit var instance: ChumianApp
            private set

        const val CHANNEL_DOWNLOAD = "download_channel"
    }
}
