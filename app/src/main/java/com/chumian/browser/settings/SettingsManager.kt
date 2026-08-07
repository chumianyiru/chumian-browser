package com.chumian.browser.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // 搜索引擎设置
    var searchEngine: String
        get() = prefs.getString("search_engine", "baidu") ?: "baidu"
        set(value) = prefs.edit().putString("search_engine", value).apply()

    // 主题设置
    var themeMode: String
        get() = prefs.getString("theme_mode", "system") ?: "system"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    // 字体大小设置
    var fontSize: Int
        get() = prefs.getInt("font_size", 100)
        set(value) = prefs.edit().putInt("font_size", value).apply()

    // 获取搜索URL
    fun getSearchUrl(query: String): String {
        return when (searchEngine) {
            "baidu" -> "https://www.baidu.com/s?wd=$query"
            "google" -> "https://www.google.com/search?q=$query"
            "bing" -> "https://www.bing.com/search?q=$query"
            "sogou" -> "https://www.sogou.com/web?query=$query"
            "360" -> "https://www.so.com/s?q=$query"
            else -> "https://www.baidu.com/s?wd=$query"
        }
    }

    // 获取搜索引擎名称
    fun getSearchEngineName(): String {
        return when (searchEngine) {
            "baidu" -> "百度"
            "google" -> "Google"
            "bing" -> "必应"
            "sogou" -> "搜狗"
            "360" -> "360搜索"
            else -> "百度"
        }
    }

    // 获取主题名称
    fun getThemeName(): String {
        return when (themeMode) {
            "light" -> "浅色"
            "dark" -> "深色"
            "system" -> "跟随系统"
            else -> "跟随系统"
        }
    }

    // 获取字体大小名称
    fun getFontSizeName(): String {
        return when (fontSize) {
            75 -> "较小"
            100 -> "默认"
            125 -> "较大"
            150 -> "大"
            else -> "默认"
        }
    }
}
