package com.chumian.browser.data.dao

import androidx.room.*
import com.chumian.browser.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY addedTime DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE isFolder = 0 ORDER BY addedTime DESC")
    fun getAllBookmarkItems(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE isFolder = 1 ORDER BY name")
    fun getAllFolders(): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE folderId = :folderId ORDER BY `order`")
    fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>>
    
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: Long): Bookmark?
    
    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getBookmarkByUrl(url: String): Bookmark?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long
    
    @Update
    suspend fun updateBookmark(bookmark: Bookmark)
    
    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
    
    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
    
    @Query("DELETE FROM bookmarks")
    suspend fun deleteAllBookmarks()
    
    @Query("SELECT COUNT(*) FROM bookmarks WHERE url = :url")
    suspend fun isBookmarked(url: String): Int
    
    @Query("SELECT * FROM bookmarks WHERE title LIKE :query OR url LIKE :query ORDER BY addedTime DESC")
    fun searchBookmarks(query: String): Flow<List<Bookmark>>
}
