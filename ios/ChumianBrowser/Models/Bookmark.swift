import Foundation

struct Bookmark: Identifiable, Codable, Equatable {
    let id: UUID
    var url: String
    var title: String
    var addedDate: Date
    
    init(url: String, title: String) {
        self.id = UUID()
        self.url = url
        self.title = title
        self.addedDate = Date()
    }
}
