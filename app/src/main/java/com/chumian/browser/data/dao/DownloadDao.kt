package com.chumian.browser.data.dao

import androidx.room.*
import com.chumian.browser.data.model.DownloadItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY addedTime DESC")
    fun getAllDownloads(): Flow<List<DownloadItem>>
    
    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY addedTime DESC")
    fun getDownloadsByStatus(status: Int): Flow<List<DownloadItem>>
    
    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadItem?
    
    @Query("SELECT * FROM downloads WHERE url = :url LIMIT 1")
    suspend fun getDownloadByUrl(url: String): DownloadItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadItem): Long
    
    @Update
    suspend fun updateDownload(download: DownloadItem)
    
    @Delete
    suspend fun deleteDownload(download: DownloadItem)
    
    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: Long)
    
    @Query("DELETE FROM downloads")
    suspend fun deleteAllDownloads()
    
    @Query("DELETE FROM downloads WHERE status = :status")
    suspend fun deleteDownloadsByStatus(status: Int)
    
    @Query("SELECT * FROM downloads WHERE fileName LIKE :query OR url LIKE :query ORDER BY addedTime DESC")
    fun searchDownloads(query: String): Flow<List<DownloadItem>>
    
    @Query("SELECT COUNT(*) FROM downloads WHERE status = :status")
    suspend fun getDownloadCountByStatus(status: Int): Int
}
