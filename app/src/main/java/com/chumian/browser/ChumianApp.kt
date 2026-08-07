package com.chumian.browser

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.chumian.browser.data.database.AppDatabase
import com.chumian.browser.data.repository.SettingsRepository
import com.chumian.browser.module.ModuleManager
import com.chumian.browser.security.SecurityManager
import com.chumian.browser.download.DownloadManager
import com.chumian.browser.bookmark.BookmarkManager
import com.chumian.browser.history.HistoryManager
import com.chumian.browser.util.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ChumianApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    lateinit var database: AppDatabase
        private set
    
    lateinit var settingsRepository: SettingsRepository
        private set
    
    lateinit var moduleManager: ModuleManager
        private set
    
    lateinit var securityManager: SecurityManager
        private set
    
    lateinit var downloadManager: DownloadManager
        private set
    
    lateinit var bookmarkManager: BookmarkManager
        private set
    
    lateinit var historyManager: HistoryManager
        private set
    
    lateinit var themeManager: ThemeManager
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化数据库
        database = AppDatabase.getInstance(this)
        
        // 初始化设置仓库
        settingsRepository = SettingsRepository(this)
        
        // 初始化主题管理器
        themeManager = ThemeManager(this)
        applyTheme()
        
        // 初始化通知渠道
        createNotificationChannels()
        
        // 初始化各管理器
        moduleManager = ModuleManager(this)
        securityManager = SecurityManager(this)
        downloadManager = DownloadManager(this)
        bookmarkManager = BookmarkManager(this)
        historyManager = HistoryManager(this)
        
        // 初始化内置模块
        applicationScope.launch {
            moduleManager.initializeBuiltInModules()
        }
    }
    
    private fun applyTheme() {
        val themeMode = settingsRepository.getThemeMode()
        themeManager.applyTheme(themeMode)
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_DOWNLOAD,
                    "下载通知",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "显示文件下载进度和状态"
                    setShowBadge(false)
                },
                NotificationChannel(
                    CHANNEL_SECURITY,
                    "安全通知",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "安全警告和威胁检测通知"
                },
                NotificationChannel(
                    CHANNEL_UPDATE,
                    "更新通知",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "应用和模块更新通知"
                },
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "通用通知",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "其他通用通知"
                }
            )
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }
    
    companion object {
        lateinit var instance: ChumianApp
            private set
        
        const val CHANNEL_DOWNLOAD = "download_channel"
        const val CHANNEL_SECURITY = "security_channel"
        const val CHANNEL_UPDATE = "update_channel"
        const val CHANNEL_GENERAL = "general_channel"
    }
}
