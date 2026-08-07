package com.chumian.browser.history

import android.content.Context
import android.content.SharedPreferences
import com.chumian.browser.data.model.HistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("history", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxHistoryItems = 500

    fun getHistory(): List<HistoryItem> {
        val json = prefs.getString("history_list", "[]")
        val type = object : TypeToken<List<HistoryItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addHistory(url: String, title: String) {
        val history = getHistory().toMutableList()
        // 移除相同URL的旧记录
        history.removeAll { it.url == url }
        // 添加到开头
        history.add(0, HistoryItem(url = url, title = title))
        // 限制最大数量
        if (history.size > maxHistoryItems) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    fun removeHistory(url: String) {
        val history = getHistory().filter { it.url != url }
        saveHistory(history)
    }

    fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<HistoryItem>) {
        val json = gson.toJson(history)
        prefs.edit().putString("history_list", json).apply()
    }
}
