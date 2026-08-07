import SwiftUI
import WebKit

struct BrowserView: View {
    @EnvironmentObject var bookmarkManager: BookmarkManager
    @EnvironmentObject var historyManager: HistoryManager
    @EnvironmentObject var downloadManager: DownloadManager
    @EnvironmentObject var settingsManager: SettingsManager
    @EnvironmentObject var tabManager: TabManager
    
    @State private var urlString = ""
    @State private var showMenu = false
    @State private var showTabs = false
    @State private var showBookmarks = false
    @State private var showHistory = false
    @State private var showDownloads = false
    @State private var showSettings = false
    @State private var showFindBar = false
    @State private var findQuery = ""
    @State private var isIncognito = false
    @State private var showSourceView = false
    @State private var sourceCode = ""
    @State private var showSecurityInfo = false
    @State private var showShareSheet = false
    @State private var progress: Double = 0
    @State private var isEditingUrl = false
    @State private var showJSAlert = false
    @State private var jsAlertMessage = ""
    
    var activeTab: BrowserTab {
        tabManager.activeTab ?? tabManager.tabs[0]
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 无痕模式提示
                if isIncognito {
                    HStack {
                        Spacer()
                        Image(systemName: "eye.slash.fill")
                            .font(.caption)
                        Text("无痕浏览模式")
                            .font(.caption)
                            .fontWeight(.medium)
                        Spacer()
                    }
                    .padding(.vertical, 6)
                    .background(Color.purple.opacity(0.15))
                    .foregroundColor(.purple)
                }
                
                // 地址栏
                HStack(spacing: 10) {
                    // 安全信息按钮
                    Button(action: { showSecurityInfo = true }) {
                        Image(systemName: isSecureUrl(activeTab.url) ? "lock.fill" : "lock.open.fill")
                            .foregroundColor(isSecureUrl(activeTab.url) ? .green : .orange)
                            .font(.system(size: 16))
                    }
                    .frame(width: 24)
                    
                    // 地址输入框
                    ZStack {
                        RoundedRectangle(cornerRadius: 10)
                            .fill(Color(.systemGray6))
                        
                        HStack(spacing: 8) {
                            if !isEditingUrl {
                                Image(systemName: "magnifyingglass")
                                    .foregroundColor(.gray)
                                    .font(.system(size: 14))
                            }
                            
                            TextField("搜索或输入网址", text: $urlString, onEditingChanged: { editing in
                                isEditingUrl = editing
                                if editing {
                                    urlString = activeTab.url
                                }
                            }) {
                                loadUrl(urlString)
                                hideKeyboard()
                            }
                            .font(.system(size: 15))
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                            .keyboardType(.webSearch)
                            
                            if !urlString.isEmpty {
                                Button(action: {
                                    urlString = ""
                                }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(.gray)
                                        .font(.system(size: 14))
                                }
                            }
                        }
                        .padding(.horizontal, 12)
                    }
                    .frame(height: 36)
                    
                    // 刷新/停止按钮
                    Button(action: {
                        if activeTab.isLoading {
                            activeTab.webView?.stopLoading()
                        } else {
                            activeTab.webView?.reload()
                        }
                    }) {
                        Image(systemName: activeTab.isLoading ? "xmark" : "arrow.clockwise")
                            .foregroundColor(.blue)
                            .font(.system(size: 16))
                    }
                    .frame(width: 24)
                    
                    // 菜单按钮
                    Button(action: { showMenu = true }) {
                        Image(systemName: "ellipsis.circle")
                            .foregroundColor(.blue)
                            .font(.system(size: 20))
                    }
                    .frame(width: 24)
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(Color(.systemBackground))
                
                // 进度条
                if activeTab.isLoading {
                    ProgressView(value: progress)
                        .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                        .padding(.horizontal, 12)
                }
                
                // 查找栏
                if showFindBar {
                    HStack(spacing: 8) {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.gray)
                        
                        TextField("在页面中查找", text: $findQuery)
                            .textFieldStyle(PlainTextFieldStyle())
                            .onChange(of: findQuery) { newValue in
                                findInPage(newValue)
                            }
                        
                        Button(action: { findPrevious() }) {
                            Image(systemName: "chevron.up")
                                .foregroundColor(.blue)
                        }
                        
                        Button(action: { findNext() }) {
                            Image(systemName: "chevron.down")
                                .foregroundColor(.blue)
                        }
                        
                        Button(action: {
                            showFindBar = false
                            findQuery = ""
                            activeTab.webView?.evaluateJavaScript("window.getSelection().removeAllRanges();", completionHandler: nil)
                        }) {
                            Image(systemName: "xmark")
                                .foregroundColor(.gray)
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(Color(.systemGray6))
                }
                
                // WebView
                ZStack {
                    WebView(
                        tab: activeTab,
                        settingsManager: settingsManager,
                        historyManager: historyManager,
                        downloadManager: downloadManager,
                        isIncognito: isIncognito,
                        onNavigationStart: {
                            progress = 0
                        },
                        onNavigationFinish: { title, url in
                            urlString = url
                            progress = 1.0
                        },
                        onUpdateProgress: { p in
                            progress = p
                        },
                        onDownload: { url, fileName in
                            // 下载处理
                        }
                    )
                    .id(activeTab.id)
                    
                    // 加载状态遮罩
                    if activeTab.isLoading && progress < 0.1 {
                        Color(.systemBackground)
                            .opacity(0.3)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                
                // 底部工具栏
                HStack(spacing: 0) {
                    // 后退
                    ToolbarButton(icon: "chevron.left", action: {
                        activeTab.webView?.goBack()
                    }, enabled: activeTab.canGoBack)
                    
                    Spacer()
                    
                    // 前进
                    ToolbarButton(icon: "chevron.right", action: {
                        activeTab.webView?.goForward()
                    }, enabled: activeTab.canGoForward)
                    
                    Spacer()
                    
                    // 分享
                    ToolbarButton(icon: "square.and.arrow.up", action: {
                        showShareSheet = true
                    })
                    
                    Spacer()
                    
                    // 书签
                    ToolbarButton(icon: bookmarkManager.isBookmarked(url: activeTab.url) ? "bookmark.fill" : "bookmark", action: {
                        bookmarkManager.toggleBookmark(url: activeTab.url, title: activeTab.title)
                    })
                    
                    Spacer()
                    
                    // 标签页
                    Button(action: { showTabs = true }) {
                        ZStack {
                            Image(systemName: "square.on.square")
                                .foregroundColor(.blue)
                                .font(.system(size: 20))
                            
                            Text("\(tabManager.tabCount)")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundColor(.blue)
                                .offset(y: -1)
                        }
                        .frame(width: 44, height: 44)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(Color(.systemBackground))
                .overlay(
                    Rectangle()
                        .frame(height: 0.5)
                        .foregroundColor(Color(.systemGray4)),
                    alignment: .top
                )
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showMenu) {
                MenuView(
                    isBookmarked: bookmarkManager.isBookmarked(url: activeTab.url),
                    isIncognito: isIncognito,
                    isDesktopMode: settingsManager.isDesktopMode,
                    currentUrl: activeTab.url,
                    onHome: { loadUrl(settingsManager.homePage) },
                    onToggleBookmark: {
                        bookmarkManager.toggleBookmark(url: activeTab.url, title: activeTab.title)
                    },
                    onViewSource: { viewSource() },
                    onFindInPage: { showFindBar = true },
                    onToggleIncognito: { toggleIncognito() },
                    onToggleDesktop: { toggleDesktopMode() },
                    onShare: { showShareSheet = true },
                    onClearCache: { clearCache() },
                    onShowBookmarks: { showBookmarks = true },
                    onShowHistory: { showHistory = true },
                    onShowDownloads: { showDownloads = true },
                    onShowSettings: { showSettings = true },
                    onAddTab: { addNewTab() }
                )
            }
            .sheet(isPresented: $showTabs) {
                TabsView()
                    .environmentObject(tabManager)
            }
            .sheet(isPresented: $showBookmarks) {
                BookmarksView(onSelect: { url in
                    loadUrl(url)
                    showBookmarks = false
                })
                .environmentObject(bookmarkManager)
            }
            .sheet(isPresented: $showHistory) {
                HistoryView(onSelect: { url in
                    loadUrl(url)
                    showHistory = false
                })
                .environmentObject(historyManager)
            }
            .sheet(isPresented: $showDownloads) {
                DownloadsView()
                    .environmentObject(downloadManager)
            }
            .sheet(isPresented: $showSettings) {
                SettingsView()
                    .environmentObject(settingsManager)
            }
            .sheet(isPresented: $showSourceView) {
                SourceCodeView(sourceCode: sourceCode, url: activeTab.url)
            }
            .alert("安全信息", isPresented: $showSecurityInfo) {
                Button("确定", role: .cancel) { }
            } message: {
                Text(getSecurityInfo())
            }
            .alert("提示", isPresented: $showJSAlert) {
                Button("确定", role: .cancel) { }
            } message: {
                Text(jsAlertMessage)
            }
            .onAppear {
                urlString = activeTab.url
            }
            .onReceive(NotificationCenter.default.publisher(for: .openNewTab)) { notification in
                if let url = notification.object as? String {
                    tabManager.addTab(url: url, title: "新标签页")
                }
            }
            .onReceive(NotificationCenter.default.publisher(for: .showJSAlert)) { notification in
                if let message = notification.object as? String {
                    jsAlertMessage = message
                    showJSAlert = true
                }
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
        .preferredColorScheme(settingsManager.themeMode.colorScheme)
    }
    
    private func loadUrl(_ url: String) {
        let trimmedUrl = url.trimmingCharacters(in: .whitespacesAndNewlines)
        
        guard !trimmedUrl.isEmpty else { return }
        
        let finalUrl: String
        if trimmedUrl.hasPrefix("http://") || trimmedUrl.hasPrefix("https://") {
            finalUrl = trimmedUrl
        } else if trimmedUrl.contains(".") && !trimmedUrl.contains(" ") {
            finalUrl = "https://" + trimmedUrl
        } else {
            finalUrl = settingsManager.getSearchUrl(trimmedUrl)
        }
        
        if let url = URL(string: finalUrl) {
            let request = URLRequest(url: url)
            activeTab.webView?.load(request)
            urlString = finalUrl
        }
    }
    
    private func isSecureUrl(_ url: String) -> Bool {
        url.hasPrefix("https://")
    }
    
    private func getSecurityInfo() -> String {
        let url = activeTab.url
        var info = "网址：\(url)\n\n"
        
        if url.hasPrefix("https://") {
            info += "🔒 安全连接\n\n"
            info += "此网站使用 HTTPS 加密连接，您的数据传输是安全的。"
        } else {
            info += "⚠️ 不安全连接\n\n"
            info += "此网站未使用 HTTPS 加密，您的数据可能被第三方窃取。"
        }
        
        return info
    }
    
    private func viewSource() {
        activeTab.webView?.evaluateJavaScript("document.documentElement.outerHTML") { result, error in
            if let html = result as? String {
                sourceCode = html
                showSourceView = true
            }
        }
    }
    
    private func findInPage(_ query: String) {
        guard !query.isEmpty else { return }
        let js = """
        (function() {
            window.find('\(query.replacingOccurrences(of: "'", with: "\\'"))', false, false, true, false, true, false);
        })();
        """
        activeTab.webView?.evaluateJavaScript(js, completionHandler: nil)
    }
    
    private func findNext() {
        let js = "window.find('', false, false, true, false, false, false);"
        activeTab.webView?.evaluateJavaScript(js, completionHandler: nil)
    }
    
    private func findPrevious() {
        let js = "window.find('', false, true, true, false, false, false);"
        activeTab.webView?.evaluateJavaScript(js, completionHandler: nil)
    }
    
    private func toggleIncognito() {
        isIncognito.toggle()
        if isIncognito {
            // 无痕模式 - 创建新标签页
            tabManager.addTab(url: "about:blank", title: "无痕浏览")
        }
    }
    
    private func toggleDesktopMode() {
        settingsManager.isDesktopMode.toggle()
        activeTab.webView?.reload()
    }
    
    private func clearCache() {
        let dataStore = WKWebsiteDataStore.default()
        let dataTypes = WKWebsiteDataStore.allWebsiteDataTypes()
        dataStore.removeData(ofTypes: dataTypes, modifiedSince: Date.distantPast) {
            // 清除完成
        }
    }
    
    private func addNewTab() {
        tabManager.addTab(url: settingsManager.homePage, title: "新标签页")
        showMenu = false
    }
    
    private func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}

struct ToolbarButton: View {
    let icon: String
    let action: () -> Void
    var enabled: Bool = true
    
    var body: some View {
        Button(action: action) {
            Image(systemName: icon)
                .foregroundColor(enabled ? .blue : .gray)
                .font(.system(size: 20))
                .frame(width: 44, height: 44)
        }
        .disabled(!enabled)
    }
}

struct MenuView: View {
    let isBookmarked: Bool
    let isIncognito: Bool
    let isDesktopMode: Bool
    let currentUrl: String
    
    let onHome: () -> Void
    let onToggleBookmark: () -> Void
    let onViewSource: () -> Void
    let onFindInPage: () -> Void
    let onToggleIncognito: () -> Void
    let onToggleDesktop: () -> Void
    let onShare: () -> Void
    let onClearCache: () -> Void
    let onShowBookmarks: () -> Void
    let onShowHistory: () -> Void
    let onShowDownloads: () -> Void
    let onShowSettings: () -> Void
    let onAddTab: () -> Void
    
    var body: some View {
        NavigationView {
            List {
                Section {
                    Button(action: onAddTab) {
                        Label("新建标签页", systemImage: "plus.square.on.square")
                    }
                    
                    Button(action: onHome) {
                        Label("首页", systemImage: "house")
                    }
                }
                
                Section {
                    Button(action: onToggleBookmark) {
                        Label(isBookmarked ? "取消书签" : "添加书签", systemImage: "bookmark")
                    }
                    
                    Button(action: onShowBookmarks) {
                        Label("书签", systemImage: "book")
                    }
                    
                    Button(action: onShowHistory) {
                        Label("历史记录", systemImage: "clock")
                    }
                    
                    Button(action: onShowDownloads) {
                        Label("下载管理", systemImage: "arrow.down.circle")
                    }
                }
                
                Section {
                    Button(action: onFindInPage) {
                        Label("页面内查找", systemImage: "magnifyingglass")
                    }
                    
                    Button(action: onViewSource) {
                        Label("查看网页源码", systemImage: "chevron.left.slash.chevron.right")
                    }
                    
                    Button(action: onToggleDesktop) {
                        Label(isDesktopMode ? "移动版页面" : "桌面版页面", systemImage: "desktopcomputer")
                    }
                }
                
                Section {
                    Button(action: onToggleIncognito) {
                        Label(isIncognito ? "退出无痕模式" : "无痕浏览", systemImage: "eye.slash")
                    }
                    
                    Button(action: onShare) {
                        Label("分享", systemImage: "square.and.arrow.up")
                    }
                    
                    Button(action: onClearCache) {
                        Label("清除缓存和Cookie", systemImage: "trash")
                    }
                }
                
                Section {
                    Button(action: onShowSettings) {
                        Label("设置", systemImage: "gear")
                    }
                }
            }
            .listStyle(InsetGroupedListStyle())
            .navigationTitle("菜单")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
        }
    }
    
    @Environment(\.presentationMode) var presentationMode
}
