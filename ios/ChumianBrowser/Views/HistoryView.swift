import SwiftUI

struct HistoryView: View {
    @EnvironmentObject var historyManager: HistoryManager
    @State private var showClearDialog = false
    
    var body: some View {
        NavigationView {
            Group {
                if historyManager.history.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "clock")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("暂无历史记录")
                            .foregroundColor(.gray)
                    }
                } else {
                    List {
                        ForEach(historyManager.history) { item in
                            Button(action: {
                                // 打开历史记录
                            }) {
                                HStack {
                                    Image(systemName: "clock.fill")
                                        .foregroundColor(.blue)
                                    VStack(alignment: .leading) {
                                        Text(item.title)
                                            .lineLimit(1)
                                        Text(item.url)
                                            .font(.caption)
                                            .foregroundColor(.gray)
                                            .lineLimit(1)
                                        Text(formatDate(item.visitTime))
                                            .font(.caption2)
                                            .foregroundColor(.gray.opacity(0.7))
                                    }
                                    Spacer()
                                    Button(action: {
                                        historyManager.removeHistory(url: item.url)
                                    }) {
                                        Image(systemName: "trash")
                                            .foregroundColor(.red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("历史记录")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    if !historyManager.history.isEmpty {
                        Button(action: { showClearDialog = true }) {
                            Image(systemName: "trash.circle")
                        }
                    }
                }
            }
            .alert("清除历史记录", isPresented: $showClearDialog) {
                Button("清除", role: .destructive) {
                    historyManager.clearHistory()
                }
                Button("取消", role: .cancel) { }
            } message: {
                Text("确定要清除所有历史记录吗？此操作不可撤销。")
            }
        }
    }
    
    private func formatDate(_ timeInterval: TimeInterval) -> String {
        let date = Date(timeIntervalSince1970: timeInterval)
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        return formatter.string(from: date)
    }
}
