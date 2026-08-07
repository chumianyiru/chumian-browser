package com.chumian.browser.bookmark

import android.content.Context
import com.chumian.browser.ChumianApp
import com.chumian.browser.data.model.Bookmark
import com.chumian.browser.data.dao.BookmarkDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BookmarkManager(private val context: Context) {
    private val bookmarkDao: BookmarkDao = (context as ChumianApp).database.bookmarkDao()
    
    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks()
    }
    
    fun getBookmarksByFolder(folderId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksByFolder(folderId)
    }
    
    fun searchBookmarks(query: String): Flow<List<Bookmark>> {
        return bookmarkDao.searchBookmarks("%$query%")
    }
    
    suspend fun addBookmark(url: String, title: String): Long {
        val existing = bookmarkDao.getBookmarkByUrl(url)
        if (existing != null) {
            return existing.id
        }
        
        val bookmark = Bookmark(
            url = url,
            title = title,
            addedTime = System.currentTimeMillis()
        )
        return bookmarkDao.insertBookmark(bookmark)
    }
    
    suspend fun addBookmark(url: String, title: String, folderId: Long): Long {
        val bookmark = Bookmark(
            url = url,
            title = title,
            folderId = folderId,
            addedTime = System.currentTimeMillis()
        )
        return bookmarkDao.insertBookmark(bookmark)
    }
    
    suspend fun addFolder(name: String, parentFolderId: Long = 0): Long {
        val folder = Bookmark(
            url = "",
            title = name,
            isFolder = true,
            parentFolderId = parentFolderId,
            addedTime = System.currentTimeMillis()
        )
        return bookmarkDao.insertBookmark(folder)
    }
    
    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.updateBookmark(bookmark)
    }
    
    suspend fun deleteBookmark(id: Long) {
        bookmarkDao.deleteBookmarkById(id)
    }
    
    suspend fun deleteAllBookmarks() {
        bookmarkDao.deleteAllBookmarks()
    }
    
    suspend fun isBookmarked(url: String): Boolean {
        return bookmarkDao.isBookmarked(url) > 0
    }
    
    suspend fun getBookmarkById(id: Long): Bookmark? {
        return bookmarkDao.getBookmarkById(id)
    }
    
    suspend fun getBookmarkByUrl(url: String): Bookmark? {
        return bookmarkDao.getBookmarkByUrl(url)
    }
    
    fun getAllFolders(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllFolders()
    }
    
    suspend fun exportBookmarks(): String {
        val bookmarks = bookmarkDao.getAllBookmarks().first()
        val sb = StringBuilder()
        sb.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n")
        sb.append("<!-- This is an automatically generated file.\n")
        sb.append("     It will be read and overwritten.\n")
        sb.append("     DO NOT EDIT! -->\n")
        sb.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n")
        sb.append("<TITLE>Bookmarks</TITLE>\n")
        sb.append("<H1>Bookmarks</H1>\n")
        sb.append("<DL><p>\n")
        
        bookmarks.filter { !it.isFolder }.forEach { bookmark ->
            sb.append("    <DT><A HREF=\"${bookmark.url}\" ADD_DATE=\"${bookmark.addedTime / 1000}\">${bookmark.title}</A>\n")
        }
        
        sb.append("</DL><p>\n")
        return sb.toString()
    }
    
    suspend fun importBookmarks(html: String): Int {
        // 简单的HTML书签导入
        var count = 0
        val regex = Regex("<A HREF=\"([^\"]+)\"[^>]*>([^<]+)</A>")
        val matches = regex.findAll(html)
        
        matches.forEach { match ->
            val url = match.groupValues[1]
            val title = match.groupValues[2]
            addBookmark(url, title)
            count++
        }
        
        return count
    }
}
