package com.chumian.browser.bookmark

import android.content.Context
import android.content.SharedPreferences
import com.chumian.browser.data.model.Bookmark
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookmarkManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getBookmarks(): List<Bookmark> {
        val json = prefs.getString("bookmark_list", "[]")
        val type = object : TypeToken<List<Bookmark>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addBookmark(url: String, title: String) {
        val bookmarks = getBookmarks().toMutableList()
        // 检查是否已存在
        if (bookmarks.any { it.url == url }) return
        bookmarks.add(0, Bookmark(url = url, title = title))
        saveBookmarks(bookmarks)
    }

    fun removeBookmark(url: String) {
        val bookmarks = getBookmarks().filter { it.url != url }
        saveBookmarks(bookmarks)
    }

    fun isBookmarked(url: String): Boolean {
        return getBookmarks().any { it.url == url }
    }

    private fun saveBookmarks(bookmarks: List<Bookmark>) {
        val json = gson.toJson(bookmarks)
        prefs.edit().putString("bookmark_list", json).apply()
    }
}
