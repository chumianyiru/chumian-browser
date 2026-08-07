import Foundation
import SwiftUI
import WebKit

class TabManager: ObservableObject {
    @Published var tabs: [BrowserTab] = []
    @Published var activeTabId: UUID?
    
    var activeTab: BrowserTab? {
        tabs.first { $0.id == activeTabId }
    }
    
    var tabCount: Int {
        tabs.count
    }
    
    init() {
        // 创建初始标签页
        addTab(url: "https://www.baidu.com", title: "百度一下")
    }
    
    func addTab(url: String = "https://www.baidu.com", title: String = "新标签页") {
        let tab = BrowserTab(url: url, title: title)
        tabs.append(tab)
        activeTabId = tab.id
    }
    
    func closeTab(_ id: UUID) {
        guard let index = tabs.firstIndex(where: { $0.id == id }) else { return }
        
        tabs.remove(at: index)
        
        // 如果关闭的是当前活动标签，切换到相邻的标签
        if activeTabId == id {
            if tabs.isEmpty {
                addTab()
            } else {
                let newIndex = min(index, tabs.count - 1)
                activeTabId = tabs[newIndex].id
            }
        }
    }
    
    func selectTab(_ id: UUID) {
        activeTabId = id
    }
    
    func updateTabTitle(_ id: UUID, title: String) {
        if let index = tabs.firstIndex(where: { $0.id == id }) {
            tabs[index].title = title
        }
    }
    
    func updateTabURL(_ id: UUID, url: String) {
        if let index = tabs.firstIndex(where: { $0.id == id }) {
            tabs[index].url = url
        }
    }
    
    func closeAllTabs() {
        tabs.removeAll()
        addTab()
    }
    
    func moveTab(from source: IndexSet, to destination: Int) {
        tabs.move(fromOffsets: source, toOffset: destination)
    }
}

class BrowserTab: Identifiable, ObservableObject {
    let id: UUID
    @Published var title: String
    @Published var url: String
    @Published var isLoading: Bool = false
    @Published var canGoBack: Bool = false
    @Published var canGoForward: Bool = false
    @Published var progress: Double = 0
    
    var webView: WKWebView?
    
    init(id: UUID = UUID(), url: String, title: String) {
        self.id = id
        self.url = url
        self.title = title
    }
}
