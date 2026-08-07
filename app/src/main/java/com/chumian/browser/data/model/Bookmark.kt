package com.chumian.browser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val title: String,
    val folderId: Long = 0,
    val favicon: String? = null,
    val addedTime: Long = System.currentTimeMillis(),
    val isFolder: Boolean = false,
    val parentFolderId: Long = 0,
    val order: Int = 0
)
