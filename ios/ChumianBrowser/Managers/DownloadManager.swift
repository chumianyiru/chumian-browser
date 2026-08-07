import Foundation
import SwiftUI

class DownloadManager: ObservableObject {
    @Published var downloads: [DownloadItem] = []
    
    private let saveKey = "downloads"
    
    init() {
        load()
    }
    
    func load() {
        guard let data = UserDefaults.standard.data(forKey: saveKey),
              let decoded = try? JSONDecoder().decode([DownloadItem].self, from: data) else {
            return
        }
        downloads = decoded
    }
    
    func save() {
        guard let encoded = try? JSONEncoder().encode(downloads) else { return }
        UserDefaults.standard.set(encoded, forKey: saveKey)
    }
    
    func addDownload(url: String, fileName: String) {
        let item = DownloadItem(url: url, fileName: fileName)
        downloads.insert(item, at: 0)
        save()
    }
    
    func removeDownload(id: UUID) {
        downloads.removeAll { $0.id == id }
        save()
    }
}
