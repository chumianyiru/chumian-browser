package com.chumian.browser.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.chumian.browser.data.repository.SettingsRepository

class ThemeManager(private val context: Context) {
    private val settingsRepository = SettingsRepository(context)
    
    fun applyTheme(themeMode: String) {
        when (themeMode) {
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "system" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
    
    fun isDarkTheme(): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> {
                val currentNightMode = context.resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
                currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
    
    fun getThemeModeName(themeMode: String): String {
        return when (themeMode) {
            "light" -> "浅色模式"
            "dark" -> "深色模式"
            "system" -> "跟随系统"
            else -> "跟随系统"
        }
    }
    
    fun getPrimaryColorList(): List<Pair<String, String>> {
        return listOf(
            "初眠紫" to "#5B6ABF",
            "蓝色" to "#2196F3",
            "绿色" to "#4CAF50",
            "橙色" to "#FF9800",
            "红色" to "#F44336",
            "粉色" to "#E91E63",
            "青色" to "#00BCD4",
            "黄色" to "#FFEB3B",
            "棕色" to "#795548",
            "灰色" to "#9E9E9E"
        )
    }
}
