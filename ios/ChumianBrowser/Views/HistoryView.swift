import SwiftUI

struct HistoryView: View {
    @EnvironmentObject var historyManager: HistoryManager
    @Environment(\.presentationMode) var presentationMode
    
    let onSelect: (String) -> Void
    
    @State private var searchText = ""
    @State private var showClearDialog = false
    
    var filteredHistory: [HistoryItem] {
        if searchText.isEmpty {
            return historyManager.history
        }
        return historyManager.searchHistory(searchText)
    }
    
    var groupedHistory: [(date: String, items: [HistoryItem])] {
        let items = filteredHistory
        let calendar = Calendar.current
        let grouped = Dictionary(grouping: items) { item -> String in
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
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 搜索栏
                SearchBar(text: $searchText, placeholder: "搜索历史记录")
                    .padding(.horizontal)
                    .padding(.vertical, 8)
                
                if filteredHistory.isEmpty {
                    VStack(spacing: 16) {
                        Spacer()
                        Image(systemName: "clock")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text(searchText.isEmpty ? "暂无历史记录" : "未找到匹配的记录")
                            .foregroundColor(.gray)
                        Spacer()
                    }
                } else {
                    List {
                        ForEach(groupedHistory, id: \.date) { group in
                            Section(header: Text(group.date)) {
                                ForEach(group.items) { item in
                                    Button(action: {
                                        onSelect(item.url)
                                        presentationMode.wrappedValue.dismiss()
                                    }) {
                                        HStack(spacing: 12) {
                                            // 网站图标占位
                                            ZStack {
                                                RoundedRectangle(cornerRadius: 6)
                                                    .fill(Color.orange.opacity(0.1))
                                                Text(String(item.title.prefix(1)).uppercased())
                                                    .font(.system(size: 14, weight: .bold))
                                                    .foregroundColor(.orange)
                                            }
                                            .frame(width: 32, height: 32)
                                            
                                            VStack(alignment: .leading, spacing: 2) {
                                                Text(item.title)
                                                    .font(.system(size: 15))
                                                    .foregroundColor(.primary)
                                                    .lineLimit(1)
                                                
                                                Text(item.url)
                                                    .font(.system(size: 12))
                                                    .foregroundColor(.gray)
                                                    .lineLimit(1)
                                                
                                                Text(formatTime(item.visitTime))
                                                    .font(.system(size: 11))
                                                    .foregroundColor(.gray.opacity(0.7))
                                            }
                                            
                                            Spacer()
                                            
                                            Image(systemName: "chevron.right")
                                                .foregroundColor(.gray)
                                                .font(.system(size: 12))
                                        }
                                        .padding(.vertical, 4)
                                    }
                                    .contextMenu {
                                        Button(action: {
                                            historyManager.removeHistory(item)
                                        }) {
                                            Label("删除", systemImage: "trash")
                                        }
                                    }
                                }
                                .onDelete { offsets in
                                    let itemsToDelete = offsets.map { group.items[$0] }
                                    itemsToDelete.forEach { historyManager.removeHistory($0) }
                                }
                            }
                        }
                    }
                    .listStyle(InsetGroupedListStyle())
                }
            }
            .navigationTitle("历史记录")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    if !filteredHistory.isEmpty {
                        Button(action: { showClearDialog = true }) {
                            Image(systemName: "trash")
                                .foregroundColor(.red)
                        }
                    }
                }
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .actionSheet(isPresented: $showClearDialog) {
                ActionSheet(
                    title: Text("清除历史记录"),
                    message: Text("选择要清除的范围"),
                    buttons: [
                        .destructive(Text("清除最近一小时")) {
                            // 清除最近一小时
                        },
                        .destructive(Text("清除最近一天")) {
                            historyManager.clearHistoryOlderThan(days: 0)
                        },
                        .destructive(Text("清除最近一周")) {
                            historyManager.clearHistoryOlderThan(days: 7)
                        },
                        .destructive(Text("清除全部历史记录")) {
                            historyManager.clearHistory()
                        },
                        .cancel()
                    ]
                )
            }
        }
    }
    
    private func formatTime(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        formatter.locale = Locale(identifier: "zh_CN")
        return formatter.string(from: date)
    }
}
