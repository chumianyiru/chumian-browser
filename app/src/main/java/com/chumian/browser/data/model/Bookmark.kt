package com.chumian.browser.data.model

data class Bookmark(
    val id: Long = System.currentTimeMillis(),
    val url: String,
    val title: String,
    val addedTime: Long = System.currentTimeMillis()
)
