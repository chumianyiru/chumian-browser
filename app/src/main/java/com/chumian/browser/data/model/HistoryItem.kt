package com.chumian.browser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val title: String,
    val favicon: String? = null,
    val visitTime: Long = System.currentTimeMillis(),
    val visitCount: Int = 1,
    val lastVisitTime: Long = System.currentTimeMillis()
)
