import Foundation

struct DownloadItem: Identifiable, Codable, Equatable {
    let id: UUID
    var url: String
    var fileName: String
    var downloadDate: Date
    var fileSize: Int64
    
    init(url: String, fileName: String, fileSize: Int64 = 0) {
        self.id = UUID()
        self.url = url
        self.fileName = fileName
        self.downloadDate = Date()
        self.fileSize = fileSize
    }
}
