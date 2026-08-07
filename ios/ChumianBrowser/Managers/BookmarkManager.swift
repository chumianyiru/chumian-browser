import Foundation
import SwiftUI

class BookmarkManager: ObservableObject {
    @Published var bookmarks: [Bookmark] = []
    @Published var folders: [String] = ["默认"]
    
    private let saveKey = "bookmarks"
    private let foldersKey = "bookmarkFolders"
    
    init() {
        load()
    }
    
    func load() {
        if let data = UserDefaults.standard.data(forKey: saveKey),
           let decoded = try? JSONDecoder().decode([Bookmark].self, from: data) {
            bookmarks = decoded
        }
        
        if let savedFolders = UserDefaults.standard.stringArray(forKey: foldersKey) {
            folders = savedFolders
        }
    }
    
    func save() {
        if let encoded = try? JSONEncoder().encode(bookmarks) {
            UserDefaults.standard.set(encoded, forKey: saveKey)
        }
        UserDefaults.standard.set(folders, forKey: foldersKey)
    }
    
    func addBookmark(url: String, title: String, folder: String = "默认") {
        guard !isBookmarked(url: url) else { return }
        let bookmark = Bookmark(title: title, url: url, folder: folder)
        bookmarks.insert(bookmark, at: 0)
        save()
    }
    
    func removeBookmark(_ bookmark: Bookmark) {
        bookmarks.removeAll { $0.id == bookmark.id }
        save()
    }
    
    func removeBookmark(url: String) {
        bookmarks.removeAll { $0.url == url }
        save()
    }
    
    func removeBookmarks(at offsets: IndexSet) {
        bookmarks.remove(atOffsets: offsets)
        save()
    }
    
    func isBookmarked(url: String) -> Bool {
        bookmarks.contains { $0.url == url }
    }
    
    func toggleBookmark(url: String, title: String) {
        if isBookmarked(url: url) {
            removeBookmark(url: url)
        } else {
            addBookmark(url: url, title: title)
        }
    }
    
    func updateBookmark(_ bookmark: Bookmark, title: String, url: String, folder: String) {
        if let index = bookmarks.firstIndex(where: { $0.id == bookmark.id }) {
            bookmarks[index].title = title
            bookmarks[index].url = url
            bookmarks[index].folder = folder
            save()
        }
    }
    
    func bookmarksInFolder(_ folder: String) -> [Bookmark] {
        bookmarks.filter { $0.folder == folder }
    }
    
    func addFolder(_ name: String) {
        guard !folders.contains(name) else { return }
        folders.append(name)
        save()
    }
    
    func removeFolder(_ name: String) {
        guard name != "默认" else { return }
        folders.removeAll { $0 == name }
        // 将该文件夹下的书签移到默认文件夹
        for index in bookmarks.indices {
            if bookmarks[index].folder == name {
                bookmarks[index].folder = "默认"
            }
        }
        save()
    }
    
    func clearAll() {
        bookmarks.removeAll()
        folders = ["默认"]
        save()
    }
    
    func searchBookmarks(_ query: String) -> [Bookmark] {
        let lowercasedQuery = query.lowercased()
        return bookmarks.filter {
            $0.title.lowercased().contains(lowercasedQuery) ||
            $0.url.lowercased().contains(lowercasedQuery)
        }
    }
}
