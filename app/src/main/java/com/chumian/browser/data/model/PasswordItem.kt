package com.chumian.browser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val username: String,
    val encryptedPassword: String,
    val siteName: String? = null,
    val favicon: String? = null,
    val createdTime: Long = System.currentTimeMillis(),
    val lastUsedTime: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
