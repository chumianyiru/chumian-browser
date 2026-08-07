import SwiftUI
import WebKit

struct BrowserView: View {
    @EnvironmentObject var bookmarkManager: BookmarkManager
    @EnvironmentObject var historyManager: HistoryManager
    @EnvironmentObject var downloadManager: DownloadManager
    @EnvironmentObject var settingsManager: SettingsManager
    
    @State private var urlString = "https://www.baidu.com"
    @State private var currentUrl = "https://www.baidu.com"
    @State private var currentTitle = "百度一下"
    @State private var isLoading = false
    @State private var canGoBack = false
    @State private var canGoForward = false
    @State private var showMenu = false
    @State private var isBookmarked = false
    @State private var showFindBar = false
    @State private var findQuery = ""
    @State private var isIncognito = false
    @State private var isDesktopMode = false
    
    var body: some View {
        VStack(spacing: 0) {
            if isIncognito {
                HStack {
                    Spacer()
                    Image(systemName: "eye.slash")
                        .font(.caption)
                    Text("无痕浏览模式")
                        .font(.caption)
                    Spacer()
                }
                .padding(.vertical, 4)
                .background(Color.blue.opacity(0.1))
            }
            
            HStack(spacing: 8) {
                Button(action: {
                    // 安全信息
                }) {
                    Image(systemName: currentUrl.hasPrefix("https://") ? "lock.fill" : "lock.open.fill")
                        .foregroundColor(currentUrl.hasPrefix("https://") ? .blue : .red)
                }
                
                TextField("搜索或输入网址", text: $urlString, onCommit: {
                    loadUrl(urlString)
                })
                .textFieldStyle(RoundedBorderTextFieldStyle())
                
                if isLoading {
                    Button(action: {
                        // 停止加载
                    }) {
                        Image(systemName: "xmark")
                    }
                } else {
                    Button(action: {
                        // 刷新
                    }) {
                        Image(systemName: "arrow.clockwise")
                    }
                }
                
                Button(action: { showMenu.toggle() }) {
                    Image(systemName: "ellipsis.circle")
                }
                .sheet(isPresented: $showMenu) {
                    MenuView(
                        isBookmarked: isBookmarked,
                        isIncognito: isIncognito,
                        isDesktopMode: isDesktopMode,
                        onForward: { },
                        onBack: { },
                        onHome: { loadUrl("https://www.baidu.com") },
                        onToggleBookmark: { },
                        onViewSource: { },
                        onFindInPage: { showFindBar.toggle() },
                        onToggleIncognito: { isIncognito.toggle() },
                        onToggleDesktop: { isDesktopMode.toggle() },
                        onShare: { },
                        onClearCache: { }
                    )
                }
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            
            if isLoading {
                ProgressView(value: 0.5)
                    .progressViewStyle(LinearProgressViewStyle())
            }
            
            if showFindBar {
                HStack(spacing: 8) {
                    TextField("在页面中查找", text: $findQuery)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    Button(action: { }) {
                        Image(systemName: "chevron.up")
                    }
                    Button(action: { }) {
                        Image(systemName: "chevron.down")
                    }
                    Button(action: {
                        showFindBar = false
                        findQuery = ""
                    }) {
                        Image(systemName: "xmark")
                    }
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
            }
            
            // WebView占位
            ZStack {
                Color.gray.opacity(0.1)
                VStack {
                    Image(systemName: "globe")
                        .font(.largeTitle)
                        .foregroundColor(.gray)
                    Text("浏览器界面")
                        .foregroundColor(.gray)
                    Text("WebView开发中...")
                        .font(.caption)
                        .foregroundColor(.gray.opacity(0.7))
                }
            }
            .frame(maxWidth: .infinity)
            .frame(maxHeight: .infinity)
        }
        .navigationBarHidden(true)
    }
    
    private func loadUrl(_ url: String) {
        let searchUrl = if url.hasPrefix("http://") || url.hasPrefix("https://") {
            url
        } else {
            settingsManager.getSearchUrl(url)
        }
        urlString = searchUrl
        currentUrl = searchUrl
    }
}

struct MenuView: View {
    let isBookmarked: Bool
    let isIncognito: Bool
    let isDesktopMode: Bool
    let onForward: () -> Void
    let onBack: () -> Void
    let onHome: () -> Void
    let onToggleBookmark: () -> Void
    let onViewSource: () -> Void
    let onFindInPage: () -> Void
    let onToggleIncognito: () -> Void
    let onToggleDesktop: () -> Void
    let onShare: () -> Void
    let onClearCache: () -> Void
    
    var body: some View {
        NavigationView {
            List {
                Button(action: onForward) {
                    Label("前进", systemImage: "arrow.right")
                }
                Button(action: onBack) {
                    Label("后退", systemImage: "arrow.left")
                }
                Button(action: onHome) {
                    Label("首页", systemImage: "house")
                }
                Button(action: onToggleBookmark) {
                    Label(isBookmarked ? "取消书签" : "添加书签", systemImage: "bookmark")
                }
                Button(action: onViewSource) {
                    Label("查看源码", systemImage: "chevron.left.slash.chevron.right")
                }
                Button(action: onFindInPage) {
                    Label("页面内查找", systemImage: "magnifyingglass")
                }
                Button(action: onToggleIncognito) {
                    Label(isIncognito ? "退出无痕模式" : "无痕浏览", systemImage: "eye.slash")
                }
                Button(action: onToggleDesktop) {
                    Label(isDesktopMode ? "移动版页面" : "桌面版页面", systemImage: "desktopcomputer")
                }
                Button(action: onShare) {
                    Label("分享", systemImage: "square.and.arrow.up")
                }
                Button(action: onClearCache) {
                    Label("清除缓存", systemImage: "trash")
                }
            }
            .navigationTitle("菜单")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
