package com.chumian.browser.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        // 搜索设置
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val SEARCH_SUGGESTIONS = booleanPreferencesKey("search_suggestions")
        val SHOW_SEARCH_BAR = booleanPreferencesKey("show_search_bar")
        
        // 外观设置
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PRIMARY_COLOR = stringPreferencesKey("primary_color")
        val FONT_SIZE = intPreferencesKey("font_size")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val AMOLED_DARK = booleanPreferencesKey("amoled_dark")
        val BOTTOM_NAVIGATION = booleanPreferencesKey("bottom_navigation")
        val HIDE_TOOLBAR_ON_SCROLL = booleanPreferencesKey("hide_toolbar_on_scroll")
        
        // 浏览设置
        val DESKTOP_MODE = booleanPreferencesKey("desktop_mode")
        val NIGHT_MODE = booleanPreferencesKey("night_mode")
        val READING_MODE = booleanPreferencesKey("reading_mode")
        val JAVASCRIPT_ENABLED = booleanPreferencesKey("javascript_enabled")
        val COOKIE_ENABLED = booleanPreferencesKey("cookie_enabled")
        val DOM_STORAGE_ENABLED = booleanPreferencesKey("dom_storage_enabled")
        val DATABASE_ENABLED = booleanPreferencesKey("database_enabled")
        val HARDWARE_ACCELERATION = booleanPreferencesKey("hardware_acceleration")
        val TEXT_ZOOM = intPreferencesKey("text_zoom")
        val DEFAULT_ZOOM = intPreferencesKey("default_zoom")
        val USER_AGENT = stringPreferencesKey("user_agent")
        val ACCEPT_LANGUAGE = stringPreferencesKey("accept_language")
        val TEXT_ENCODING = stringPreferencesKey("text_encoding")
        
        // 隐私设置
        val INCOGNITO_MODE = booleanPreferencesKey("incognito_mode")
        val DO_NOT_TRACK = booleanPreferencesKey("do_not_track")
        val THIRD_PARTY_COOKIES = booleanPreferencesKey("third_party_cookies")
        val CLEAR_DATA_ON_EXIT = booleanPreferencesKey("clear_data_on_exit")
        val REMEMBER_PASSWORDS = booleanPreferencesKey("remember_passwords")
        val AUTOFILL_ENABLED = booleanPreferencesKey("autofill_enabled")
        val SAFE_BROWSING = booleanPreferencesKey("safe_browsing")
        val AD_BLOCK = booleanPreferencesKey("ad_block")
        val TRACKING_PROTECTION = booleanPreferencesKey("tracking_protection")
        val FINGERPRINT_PROTECTION = booleanPreferencesKey("fingerprint_protection")
        
        // 下载设置
        val DOWNLOAD_DIRECTORY = stringPreferencesKey("download_directory")
        val ASK_BEFORE_DOWNLOAD = booleanPreferencesKey("ask_before_download")
        val DOWNLOAD_NOTIFICATION = booleanPreferencesKey("download_notification")
        val DOWNLOAD_OVER_WIFI_ONLY = booleanPreferencesKey("download_over_wifi_only")
        val MAX_PARALLEL_DOWNLOADS = intPreferencesKey("max_parallel_downloads")
        
        // 高级设置
        val ALLOW_FILE_ACCESS = booleanPreferencesKey("allow_file_access")
        val ALLOW_CONTENT_ACCESS = booleanPreferencesKey("allow_content_access")
        val ALLOW_UNIVERSAL_ACCESS_FROM_FILE = booleanPreferencesKey("allow_universal_access_from_file")
        val SUPPORT_ZOOM = booleanPreferencesKey("support_zoom")
        val BUILT_IN_ZOOM_CONTROLS = booleanPreferencesKey("built_in_zoom_controls")
        val DISPLAY_ZOOM_CONTROLS = booleanPreferencesKey("display_zoom_controls")
        val LOAD_WITH_OVERVIEW_MODE = booleanPreferencesKey("load_with_overview_mode")
        val USE_WIDE_VIEW_PORT = booleanPreferencesKey("use_wide_view_port")
        val SAVE_FORM_DATA = booleanPreferencesKey("save_form_data")
        val SAVE_PASSWORD = booleanPreferencesKey("save_password")
        
        // 开发者设置
        val DEVTOOLS_ENABLED = booleanPreferencesKey("devtools_enabled")
        val REMOTE_DEBUGGING = booleanPreferencesKey("remote_debugging")
        val WEBGL_ENABLED = booleanPreferencesKey("webgl_enabled")
        val WEBRTC_ENABLED = booleanPreferencesKey("webrtc_enabled")
        val EXPERIMENTAL_FEATURES = booleanPreferencesKey("experimental_features")
    }
    
    // 搜索设置
    val searchEngine: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SEARCH_ENGINE] ?: "baidu"
        }
    
    suspend fun getSearchEngine(): String {
        return context.dataStore.data.map { it[SEARCH_ENGINE] ?: "baidu" }.first()
    }
    
    suspend fun setSearchEngine(engine: String) {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_ENGINE] = engine
        }
    }
    
    // 主题设置
    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE] ?: "system"
        }
    
    suspend fun getThemeMode(): String {
        return context.dataStore.data.map { it[THEME_MODE] ?: "system" }.first()
    }
    
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }
    
    // 桌面模式
    val desktopMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DESKTOP_MODE] ?: false
        }
    
    suspend fun getDesktopMode(): Boolean {
        return context.dataStore.data.map { it[DESKTOP_MODE] ?: false }.first()
    }
    
    suspend fun setDesktopMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DESKTOP_MODE] = enabled
        }
    }
    
    // 夜间模式
    val nightMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NIGHT_MODE] ?: false
        }
    
    suspend fun getNightMode(): Boolean {
        return context.dataStore.data.map { it[NIGHT_MODE] ?: false }.first()
    }
    
    suspend fun setNightMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NIGHT_MODE] = enabled
        }
    }
    
    // 字体大小
    val fontSize: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[FONT_SIZE] ?: 100
        }
    
    suspend fun getFontSize(): Int {
        return context.dataStore.data.map { it[FONT_SIZE] ?: 100 }.first()
    }
    
    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = size
        }
    }
    
    // JavaScript
    val javascriptEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[JAVASCRIPT_ENABLED] ?: true
        }
    
    suspend fun getJavascriptEnabled(): Boolean {
        return context.dataStore.data.map { it[JAVASCRIPT_ENABLED] ?: true }.first()
    }
    
    suspend fun setJavascriptEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[JAVASCRIPT_ENABLED] = enabled
        }
    }
    
    // Cookie
    val cookieEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COOKIE_ENABLED] ?: true
        }
    
    suspend fun getCookieEnabled(): Boolean {
        return context.dataStore.data.map { it[COOKIE_ENABLED] ?: true }.first()
    }
    
    suspend fun setCookieEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COOKIE_ENABLED] = enabled
        }
    }
    
    // 广告拦截
    val adBlock: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AD_BLOCK] ?: false
        }
    
    suspend fun getAdBlock(): Boolean {
        return context.dataStore.data.map { it[AD_BLOCK] ?: false }.first()
    }
    
    suspend fun setAdBlock(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AD_BLOCK] = enabled
        }
    }
    
    // 安全浏览
    val safeBrowsing: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SAFE_BROWSING] ?: true
        }
    
    suspend fun getSafeBrowsing(): Boolean {
        return context.dataStore.data.map { it[SAFE_BROWSING] ?: true }.first()
    }
    
    suspend fun setSafeBrowsing(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SAFE_BROWSING] = enabled
        }
    }
    
    // 记住密码
    val rememberPasswords: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[REMEMBER_PASSWORDS] ?: true
        }
    
    suspend fun getRememberPasswords(): Boolean {
        return context.dataStore.data.map { it[REMEMBER_PASSWORDS] ?: true }.first()
    }
    
    suspend fun setRememberPasswords(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_PASSWORDS] = enabled
        }
    }
    
    // 下载目录
    val downloadDirectory: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DOWNLOAD_DIRECTORY] ?: "Download/ChumianBrowser"
        }
    
    suspend fun getDownloadDirectory(): String {
        return context.dataStore.data.map { it[DOWNLOAD_DIRECTORY] ?: "Download/ChumianBrowser" }.first()
    }
    
    suspend fun setDownloadDirectory(directory: String) {
        context.dataStore.edit { preferences ->
            preferences[DOWNLOAD_DIRECTORY] = directory
        }
    }
    
    // 底部导航
    val bottomNavigation: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BOTTOM_NAVIGATION] ?: true
        }
    
    suspend fun getBottomNavigation(): Boolean {
        return context.dataStore.data.map { it[BOTTOM_NAVIGATION] ?: true }.first()
    }
    
    suspend fun setBottomNavigation(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BOTTOM_NAVIGATION] = enabled
        }
    }
    
    // 开发者工具
    val devtoolsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DEVTOOLS_ENABLED] ?: true
        }
    
    suspend fun getDevtoolsEnabled(): Boolean {
        return context.dataStore.data.map { it[DEVTOOLS_ENABLED] ?: true }.first()
    }
    
    suspend fun setDevtoolsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DEVTOOLS_ENABLED] = enabled
        }
    }
    
    // 用户代理
    val userAgent: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_AGENT] ?: ""
        }
    
    suspend fun getUserAgent(): String {
        return context.dataStore.data.map { it[USER_AGENT] ?: "" }.first()
    }
    
    suspend fun setUserAgent(ua: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_AGENT] = ua
        }
    }
}

