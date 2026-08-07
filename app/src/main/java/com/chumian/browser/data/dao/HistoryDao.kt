package com.chumian.browser.data.dao

import androidx.room.*
import com.chumian.browser.data.model.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY visitTime DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>
    
    @Query("SELECT * FROM history ORDER BY visitTime DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<HistoryItem>>
    
    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Long): HistoryItem?
    
    @Query("SELECT * FROM history WHERE url = :url LIMIT 1")
    suspend fun getHistoryByUrl(url: String): HistoryItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryItem): Long
    
    @Update
    suspend fun updateHistory(history: HistoryItem)
    
    @Delete
    suspend fun deleteHistory(history: HistoryItem)
    
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)
    
    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()
    
    @Query("DELETE FROM history WHERE visitTime < :beforeTime")
    suspend fun deleteHistoryBefore(beforeTime: Long)
    
    @Query("SELECT * FROM history WHERE title LIKE :query OR url LIKE :query ORDER BY visitTime DESC")
    fun searchHistory(query: String): Flow<List<HistoryItem>>
    
    @Query("SELECT COUNT(*) FROM history")
    suspend fun getHistoryCount(): Int
}
