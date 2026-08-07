package com.chumian.browser.data.model

data class Tab(
    val id: Long,
    var url: String,
    var title: String,
    var isActive: Boolean,
    var favicon: String? = null,
    var lastAccessed: Long = System.currentTimeMillis()
)
