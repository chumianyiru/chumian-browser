import Foundation

struct HistoryItem: Identifiable, Codable, Equatable {
    let id: UUID
    var title: String
    var url: String
    var visitTime: Date
    var visitCount: Int
    
    init(id: UUID = UUID(), title: String, url: String, visitTime: Date = Date(), visitCount: Int = 1) {
        self.id = id
        self.title = title
        self.url = url
        self.visitTime = visitTime
        self.visitCount = visitCount
    }
}
