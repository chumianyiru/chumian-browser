import SwiftUI

@main
struct ChumianBrowserApp: App {
    @StateObject private var bookmarkManager = BookmarkManager()
    @StateObject private var historyManager = HistoryManager()
    @StateObject private var downloadManager = DownloadManager()
    @StateObject private var settingsManager = SettingsManager()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(bookmarkManager)
                .environmentObject(historyManager)
                .environmentObject(downloadManager)
                .environmentObject(settingsManager)
        }
    }
}
