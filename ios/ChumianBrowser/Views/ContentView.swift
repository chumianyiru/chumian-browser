import SwiftUI

struct ContentView: View {
    @StateObject private var bookmarkManager = BookmarkManager()
    @StateObject private var historyManager = HistoryManager()
    @StateObject private var downloadManager = DownloadManager()
    @StateObject private var settingsManager = SettingsManager()
    @StateObject private var tabManager = TabManager()
    
    var body: some View {
        BrowserView()
            .environmentObject(bookmarkManager)
            .environmentObject(historyManager)
            .environmentObject(downloadManager)
            .environmentObject(settingsManager)
            .environmentObject(tabManager)
    }
}
