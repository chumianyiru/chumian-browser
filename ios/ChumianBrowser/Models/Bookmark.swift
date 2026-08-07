import Foundation

struct Bookmark: Identifiable, Codable, Equatable {
    let id: UUID
    var title: String
    var url: String
    var dateAdded: Date
    var folder: String
    
    init(id: UUID = UUID(), title: String, url: String, dateAdded: Date = Date(), folder: String = "默认") {
        self.id = id
        self.title = title
        self.url = url
        self.dateAdded = dateAdded
        self.folder = folder
    }
}
