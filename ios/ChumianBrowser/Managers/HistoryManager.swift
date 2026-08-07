import Foundation
import SwiftUI

class HistoryManager: ObservableObject {
    @Published var history: [HistoryItem] = []
    
    private let saveKey = "history"
    private let maxHistory = 500
    
    init() {
        load()
    }
    
    func load() {
        guard let data = UserDefaults.standard.data(forKey: saveKey),
              let decoded = try? JSONDecoder().decode([HistoryItem].self, from: data) else {
            return
        }
        history = decoded
    }
    
    func save() {
        guard let encoded = try? JSONEncoder().encode(history) else { return }
        UserDefaults.standard.set(encoded, forKey: saveKey)
    }
    
    func addHistory(url: String, title: String) {
        // 移除重复的URL
        history.removeAll { $0.url == url }
        // 添加到开头
        let item = HistoryItem(url: url, title: title)
        history.insert(item, at: 0)
        // 限制最大数量
        if history.count > maxHistory {
            history = Array(history.prefix(maxHistory))
        }
        save()
    }
    
    func removeHistory(url: String) {
        history.removeAll { $0.url == url }
        save()
    }
    
    func clearHistory() {
        history.removeAll()
        save()
    }
}
