import Foundation

enum DownloadStatus: String, Codable {
    case downloading
    case completed
    case failed
    case paused
}

struct DownloadItem: Identifiable, Codable, Equatable {
    let id: UUID
    var url: String
    var fileName: String
    var downloadDate: Date
    var fileSize: Int64
    var downloadedSize: Int64
    var status: DownloadStatus
    var localPath: String?
    
    init(id: UUID = UUID(), url: String, fileName: String, fileSize: Int64 = 0, downloadedSize: Int64 = 0, status: DownloadStatus = .downloading, localPath: String? = nil) {
        self.id = id
        self.url = url
        self.fileName = fileName
        self.downloadDate = Date()
        self.fileSize = fileSize
        self.downloadedSize = downloadedSize
        self.status = status
        self.localPath = localPath
    }
    
    var progress: Double {
        guard fileSize > 0 else { return 0 }
        return Double(downloadedSize) / Double(fileSize)
    }
}
