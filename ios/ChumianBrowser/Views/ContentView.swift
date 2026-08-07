import SwiftUI

struct ContentView: View {
    @StateObject private var bookmarkManager = BookmarkManager()
    @StateObject private var historyManager = HistoryManager()
    @StateObject private var downloadManager = DownloadManager()
    @StateObject private var settingsManager = SettingsManager()
    
    var body: some View {
        TabView {
            BrowserView()
                .tabItem {
                    Image(systemName: "globe")
                    Text("首页")
                }
            BookmarksView()
                .tabItem {
                    Image(systemName: "bookmark")
                    Text("书签")
                }
            HistoryView()
                .tabItem {
                    Image(systemName: "clock")
                    Text("历史")
                }
            DownloadsView()
                .tabItem {
                    Image(systemName: "arrow.down.circle")
                    Text("下载")
                }
            SettingsView()
                .tabItem {
                    Image(systemName: "gear")
                    Text("设置")
                }
        }
        .environmentObject(bookmarkManager)
        .environmentObject(historyManager)
        .environmentObject(downloadManager)
        .environmentObject(settingsManager)
    }
}
