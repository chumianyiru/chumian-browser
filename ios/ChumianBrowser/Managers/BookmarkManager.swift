import Foundation
import SwiftUI

class BookmarkManager: ObservableObject {
    @Published var bookmarks: [Bookmark] = []
    
    private let saveKey = "bookmarks"
    
    init() {
        load()
    }
    
    func load() {
        guard let data = UserDefaults.standard.data(forKey: saveKey),
              let decoded = try? JSONDecoder().decode([Bookmark].self, from: data) else {
            return
        }
        bookmarks = decoded
    }
    
    func save() {
        guard let encoded = try? JSONEncoder().encode(bookmarks) else { return }
        UserDefaults.standard.set(encoded, forKey: saveKey)
    }
    
    func addBookmark(url: String, title: String) {
        let bookmark = Bookmark(url: url, title: title)
        bookmarks.insert(bookmark, at: 0)
        save()
    }
    
    func removeBookmark(url: String) {
        bookmarks.removeAll { $0.url == url }
        save()
    }
    
    func isBookmarked(url: String) -> Bool {
        bookmarks.contains { $0.url == url }
    }
}
