package com.chumian.browser.data.model

data class HistoryItem(
    val id: Long = System.currentTimeMillis(),
    val url: String,
    val title: String,
    val visitTime: Long = System.currentTimeMillis()
)
