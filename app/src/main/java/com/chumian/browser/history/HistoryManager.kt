package com.chumian.browser.history

import android.content.Context
import com.chumian.browser.ChumianApp
import com.chumian.browser.data.model.HistoryItem
import com.chumian.browser.data.dao.HistoryDao
import kotlinx.coroutines.flow.Flow

class HistoryManager(private val context: Context) {
    private val historyDao: HistoryDao = (context as ChumianApp).database.historyDao()
    
    fun getAllHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory()
    }
    
    fun getRecentHistory(limit: Int = 50): Flow<List<HistoryItem>> {
        return historyDao.getRecentHistory(limit)
    }
    
    fun searchHistory(query: String): Flow<List<HistoryItem>> {
        return historyDao.searchHistory("%$query%")
    }
    
    suspend fun addHistory(url: String, title: String): Long {
        val existing = historyDao.getHistoryByUrl(url)
        if (existing != null) {
            val updated = existing.copy(
                visitCount = existing.visitCount + 1,
                lastVisitTime = System.currentTimeMillis(),
                title = title.ifEmpty { existing.title }
            )
            historyDao.updateHistory(updated)
            return existing.id
        }
        
        val history = HistoryItem(
            url = url,
            title = title.ifEmpty { url },
            visitTime = System.currentTimeMillis(),
            lastVisitTime = System.currentTimeMillis(),
            visitCount = 1
        )
        return historyDao.insertHistory(history)
    }
    
    suspend fun updateHistory(history: HistoryItem) {
        historyDao.updateHistory(history)
    }
    
    suspend fun deleteHistory(id: Long) {
        historyDao.deleteHistoryById(id)
    }
    
    suspend fun clearHistory() {
        historyDao.deleteAllHistory()
    }
    
    suspend fun deleteHistoryBefore(beforeTime: Long) {
        historyDao.deleteHistoryBefore(beforeTime)
    }
    
    suspend fun getHistoryById(id: Long): HistoryItem? {
        return historyDao.getHistoryById(id)
    }
    
    suspend fun getHistoryByUrl(url: String): HistoryItem? {
        return historyDao.getHistoryByUrl(url)
    }
    
    suspend fun getHistoryCount(): Int {
        return historyDao.getHistoryCount()
    }
    
    suspend fun clearOldHistory(days: Int = 30) {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        historyDao.deleteHistoryBefore(cutoffTime)
    }
    
    fun getHistoryGroupedByDate(): Map<String, List<HistoryItem>> {
        // 这个方法需要在协程中调用
        return emptyMap()
    }
}
