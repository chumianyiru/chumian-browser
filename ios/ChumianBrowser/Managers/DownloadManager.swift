import Foundation
import SwiftUI

class DownloadManager: ObservableObject {
    @Published var downloads: [DownloadItem] = []
    
    private let saveKey = "downloads"
    private let fileManager = FileManager.default
    
    var downloadsDirectory: URL {
        let paths = fileManager.urls(for: .documentDirectory, in: .userDomainMask)
        let downloadDir = paths[0].appendingPathComponent("Downloads", isDirectory: true)
        if !fileManager.fileExists(atPath: downloadDir.path) {
            try? fileManager.createDirectory(at: downloadDir, withIntermediateDirectories: true)
        }
        return downloadDir
    }
    
    init() {
        load()
    }
    
    func load() {
        if let data = UserDefaults.standard.data(forKey: saveKey),
           let decoded = try? JSONDecoder().decode([DownloadItem].self, from: data) {
            downloads = decoded
        }
    }
    
    func save() {
        if let encoded = try? JSONEncoder().encode(downloads) {
            UserDefaults.standard.set(encoded, forKey: saveKey)
        }
    }
    
    func addDownload(url: String, fileName: String, fileSize: Int64 = 0) {
        let item = DownloadItem(url: url, fileName: fileName, fileSize: fileSize)
        downloads.insert(item, at: 0)
        save()
    }
    
    func updateDownloadProgress(id: UUID, downloadedSize: Int64, totalSize: Int64? = nil) {
        if let index = downloads.firstIndex(where: { $0.id == id }) {
            downloads[index].downloadedSize = downloadedSize
            if let total = totalSize {
                downloads[index].fileSize = total
            }
            save()
        }
    }
    
    func completeDownload(id: UUID, localPath: String) {
        if let index = downloads.firstIndex(where: { $0.id == id }) {
            downloads[index].status = .completed
            downloads[index].localPath = localPath
            save()
        }
    }
    
    func failDownload(id: UUID) {
        if let index = downloads.firstIndex(where: { $0.id == id }) {
            downloads[index].status = .failed
            save()
        }
    }
    
    func pauseDownload(id: UUID) {
        if let index = downloads.firstIndex(where: { $0.id == id }) {
            downloads[index].status = .paused
            save()
        }
    }
    
    func resumeDownload(id: UUID) {
        if let index = downloads.firstIndex(where: { $0.id == id }) {
            downloads[index].status = .downloading
            save()
        }
    }
    
    func removeDownload(_ item: DownloadItem) {
        // 删除本地文件
        if let path = item.localPath {
            let fileURL = URL(fileURLWithPath: path)
            try? fileManager.removeItem(at: fileURL)
        }
        downloads.removeAll { $0.id == item.id }
        save()
    }
    
    func removeDownload(at offsets: IndexSet) {
        offsets.forEach { index in
            let item = downloads[index]
            if let path = item.localPath {
                let fileURL = URL(fileURLWithPath: path)
                try? fileManager.removeItem(at: fileURL)
            }
        }
        downloads.remove(atOffsets: offsets)
        save()
    }
    
    func clearCompleted() {
        downloads.removeAll { $0.status == .completed }
        save()
    }
    
    func clearAll() {
        // 删除所有下载的文件
        for item in downloads {
            if let path = item.localPath {
                let fileURL = URL(fileURLWithPath: path)
                try? fileManager.removeItem(at: fileURL)
            }
        }
        downloads.removeAll()
        save()
    }
    
    var activeDownloads: [DownloadItem] {
        downloads.filter { $0.status == .downloading || $0.status == .paused }
    }
    
    var completedDownloads: [DownloadItem] {
        downloads.filter { $0.status == .completed }
    }
    
    func formattedFileSize(_ size: Int64) -> String {
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useAll]
        formatter.countStyle = .file
        return formatter.string(fromByteCount: size)
    }
    
    func fileExists(for item: DownloadItem) -> Bool {
        guard let path = item.localPath else { return false }
        return fileManager.fileExists(atPath: path)
    }
}
