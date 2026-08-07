import Foundation
import SwiftUI

class HistoryManager: ObservableObject {
    @Published var history: [HistoryItem] = []
    
    private let saveKey = "history"
    private let maxHistory = 1000
    
    init() {
        load()
    }
    
    func load() {
        if let data = UserDefaults.standard.data(forKey: saveKey),
           let decoded = try? JSONDecoder().decode([HistoryItem].self, from: data) {
            history = decoded
        }
    }
    
    func save() {
        if let encoded = try? JSONEncoder().encode(history) {
            UserDefaults.standard.set(encoded, forKey: saveKey)
        }
    }
    
    func addHistory(url: String, title: String) {
        // 检查是否已存在相同URL
        if let index = history.firstIndex(where: { $0.url == url }) {
            // 更新访问时间和次数
            history[index].visitTime = Date()
            history[index].visitCount += 1
            // 移到最前面
            let item = history.remove(at: index)
            history.insert(item, at: 0)
        } else {
            // 添加新记录
            let item = HistoryItem(title: title, url: url)
            history.insert(item, at: 0)
        }
        
        // 限制最大数量
        if history.count > maxHistory {
            history = Array(history.prefix(maxHistory))
        }
        
        save()
    }
    
    func removeHistory(_ item: HistoryItem) {
        history.removeAll { $0.id == item.id }
        save()
    }
    
    func removeHistory(url: String) {
        history.removeAll { $0.url == url }
        save()
    }
    
    func removeHistory(at offsets: IndexSet) {
        history.remove(atOffsets: offsets)
        save()
    }
    
    func clearHistory() {
        history.removeAll()
        save()
    }
    
    func clearHistoryOlderThan(days: Int) {
        let cutoffDate = Calendar.current.date(byAdding: .day, value: -days, to: Date()) ?? Date()
        history.removeAll { $0.visitTime < cutoffDate }
        save()
    }
    
    func searchHistory(_ query: String) -> [HistoryItem] {
        let lowercasedQuery = query.lowercased()
        return history.filter {
            $0.title.lowercased().contains(lowercasedQuery) ||
            $0.url.lowercased().contains(lowercasedQuery)
        }
    }
    
    // 按日期分组的历史记录
    var groupedHistory: [(date: String, items: [HistoryItem])] {
        let calendar = Calendar.current
        let grouped = Dictionary(grouping: history) { item -> String in
            if calendar.isDateInToday(item.visitTime) {
                return "今天"
            } else if calendar.isDateInYesterday(item.visitTime) {
                return "昨天"
            } else {
                let formatter = DateFormatter()
                formatter.dateFormat = "yyyy年MM月dd日"
                formatter.locale = Locale(identifier: "zh_CN")
                return formatter.string(from: item.visitTime)
            }
        }
        return grouped.sorted { $0.value.first?.visitTime ?? Date() > $1.value.first?.visitTime ?? Date() }
    }
}
