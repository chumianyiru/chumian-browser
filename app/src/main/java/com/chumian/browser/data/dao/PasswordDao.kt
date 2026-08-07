package com.chumian.browser.data.dao

import androidx.room.*
import com.chumian.browser.data.model.PasswordItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY lastUsedTime DESC")
    fun getAllPasswords(): Flow<List<PasswordItem>>
    
    @Query("SELECT * FROM passwords WHERE isFavorite = 1 ORDER BY lastUsedTime DESC")
    fun getFavoritePasswords(): Flow<List<PasswordItem>>
    
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Long): PasswordItem?
    
    @Query("SELECT * FROM passwords WHERE url = :url LIMIT 1")
    suspend fun getPasswordByUrl(url: String): PasswordItem?
    
    @Query("SELECT * FROM passwords WHERE url LIKE :query OR username LIKE :query OR siteName LIKE :query ORDER BY lastUsedTime DESC")
    fun searchPasswords(query: String): Flow<List<PasswordItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordItem): Long
    
    @Update
    suspend fun updatePassword(password: PasswordItem)
    
    @Delete
    suspend fun deletePassword(password: PasswordItem)
    
    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: Long)
    
    @Query("DELETE FROM passwords")
    suspend fun deleteAllPasswords()
    
    @Query("SELECT COUNT(*) FROM passwords")
    suspend fun getPasswordCount(): Int
    
    @Query("UPDATE passwords SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}
