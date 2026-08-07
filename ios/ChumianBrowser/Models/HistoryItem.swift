import Foundation

struct HistoryItem: Identifiable, Codable, Equatable {
    let id: UUID
    var url: String
    var title: String
    var visitTime: Date
    
    init(url: String, title: String) {
        self.id = UUID()
        self.url = url
        self.title = title
        self.visitTime = Date()
    }
}
