import SwiftUI
import WebKit

struct SettingsView: View {
    @EnvironmentObject var settingsManager: SettingsManager
    @EnvironmentObject var historyManager: HistoryManager
    @EnvironmentObject var bookmarkManager: BookmarkManager
    @EnvironmentObject var downloadManager: DownloadManager
    @Environment(\.presentationMode) var presentationMode
    
    @State private var showClearDataDialog = false
    @State private var showAboutView = false
    
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("通用设置")) {
                    // 搜索引擎
                    NavigationLink(destination: SearchEngineSettingsView()) {
                        HStack {
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("搜索引擎")
                            Spacer()
                            Text(settingsManager.searchEngine.displayName)
                                .foregroundColor(.gray)
                        }
                    }
                    
                    // 首页设置
                    NavigationLink(destination: HomePageSettingsView()) {
                        HStack {
                            Image(systemName: "house")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("首页设置")
                            Spacer()
                            Text(settingsManager.homePage)
                                .font(.caption)
                                .foregroundColor(.gray)
                                .lineLimit(1)
                                .frame(maxWidth: 120)
                        }
                    }
                }
                
                Section(header: Text("外观")) {
                    // 主题
                    NavigationLink(destination: ThemeSettingsView()) {
                        HStack {
                            Image(systemName: "paintpalette")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("主题模式")
                            Spacer()
                            Text(settingsManager.themeMode.displayName)
                                .foregroundColor(.gray)
                        }
                    }
                    
                    // 字体大小
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "textformat.size")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("字体大小")
                            Spacer()
                            Text("\(settingsManager.fontSize)%")
                                .foregroundColor(.gray)
                        }
                        
                        Slider(value: Binding(
                            get: { Double(settingsManager.fontSize) },
                            set: { settingsManager.fontSize = Int($0) }
                        ), in: 50...200, step: 10)
                        .padding(.leading, 30)
                        
                        HStack {
                            Text("小")
                                .font(.caption)
                                .foregroundColor(.gray)
                            Spacer()
                            Text("大")
                                .font(.caption)
                                .foregroundColor(.gray)
                        }
                        .padding(.leading, 30)
                    }
                    .padding(.vertical, 8)
                }
                
                Section(header: Text("浏览设置")) {
                    Toggle(isOn: $settingsManager.isDesktopMode) {
                        HStack {
                            Image(systemName: "desktopcomputer")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("桌面模式")
                        }
                    }
                    
                    Toggle(isOn: $settingsManager.blockPopups) {
                        HStack {
                            Image(systemName: "exclamationmark.shield")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("阻止弹窗")
                        }
                    }
                    
                    Toggle(isOn: $settingsManager.saveHistory) {
                        HStack {
                            Image(systemName: "clock")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("保存历史记录")
                        }
                    }
                }
                
                Section(header: Text("隐私与安全")) {
                    Button(action: { showClearDataDialog = true }) {
                        HStack {
                            Image(systemName: "trash")
                                .foregroundColor(.red)
                                .frame(width: 30)
                            Text("清除浏览数据")
                                .foregroundColor(.primary)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                    
                    NavigationLink(destination: PrivacyInfoView()) {
                        HStack {
                            Image(systemName: "lock.shield")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("隐私说明")
                        }
                    }
                }
                
                Section(header: Text("关于")) {
                    Button(action: { showAboutView = true }) {
                        HStack {
                            Image(systemName: "info.circle")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text("关于初眠浏览器")
                                .foregroundColor(.primary)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                    .sheet(isPresented: $showAboutView) {
                        AboutView()
                    }
                }
            }
            .listStyle(InsetGroupedListStyle())
            .navigationTitle("设置")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .fontWeight(.bold)
                }
            }
            .actionSheet(isPresented: $showClearDataDialog) {
                ActionSheet(
                    title: Text("清除浏览数据"),
                    message: Text("选择要清除的数据类型"),
                    buttons: [
                        .destructive(Text("清除缓存")) {
                            clearCache()
                        },
                        .destructive(Text("清除Cookie")) {
                            clearCookies()
                        },
                        .destructive(Text("清除历史记录")) {
                            historyManager.clearHistory()
                        },
                        .destructive(Text("清除全部数据")) {
                            clearAllData()
                        },
                        .cancel()
                    ]
                )
            }
        }
    }
    
    private func clearCache() {
        let dataStore = WKWebsiteDataStore.default()
        let dataTypes = Set([WKWebsiteDataTypeDiskCache, WKWebsiteDataTypeMemoryCache])
        dataStore.removeData(ofTypes: dataTypes, modifiedSince: Date.distantPast) {}
    }
    
    private func clearCookies() {
        let dataStore = WKWebsiteDataStore.default()
        let dataTypes = Set([WKWebsiteDataTypeCookies])
        dataStore.removeData(ofTypes: dataTypes, modifiedSince: Date.distantPast) {}
    }
    
    private func clearAllData() {
        let dataStore = WKWebsiteDataStore.default()
        let dataTypes = WKWebsiteDataStore.allWebsiteDataTypes()
        dataStore.removeData(ofTypes: dataTypes, modifiedSince: Date.distantPast) {}
        historyManager.clearHistory()
    }
}

struct SearchEngineSettingsView: View {
    @EnvironmentObject var settingsManager: SettingsManager
    
    var body: some View {
        List {
            ForEach(SearchEngine.allCases) { engine in
                Button(action: {
                    settingsManager.searchEngine = engine
                }) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(engine.displayName)
                                .foregroundColor(.primary)
                            Text(engine.homeUrl)
                                .font(.caption)
                                .foregroundColor(.gray)
                        }
                        
                        Spacer()
                        
                        if settingsManager.searchEngine == engine {
                            Image(systemName: "checkmark")
                                .foregroundColor(.blue)
                        }
                    }
                }
            }
        }
        .navigationTitle("搜索引擎")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct ThemeSettingsView: View {
    @EnvironmentObject var settingsManager: SettingsManager
    
    var body: some View {
        List {
            ForEach(ThemeMode.allCases) { mode in
                Button(action: {
                    settingsManager.themeMode = mode
                }) {
                    HStack {
                        HStack(spacing: 12) {
                            Image(systemName: themeIcon(for: mode))
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            Text(mode.displayName)
                                .foregroundColor(.primary)
                        }
                        
                        Spacer()
                        
                        if settingsManager.themeMode == mode {
                            Image(systemName: "checkmark")
                                .foregroundColor(.blue)
                        }
                    }
                }
            }
        }
        .navigationTitle("主题模式")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    private func themeIcon(for mode: ThemeMode) -> String {
        switch mode {
        case .system:
            return "gearshape"
        case .light:
            return "sun.max.fill"
        case .dark:
            return "moon.fill"
        }
    }
}

struct HomePageSettingsView: View {
    @EnvironmentObject var settingsManager: SettingsManager
    @State private var homePageText: String
    
    init() {
        _homePageText = State(initialValue: "")
    }
    
    var body: some View {
        Form {
            Section(header: Text("首页网址")) {
                TextField("首页网址", text: $homePageText)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .onAppear {
                        homePageText = settingsManager.homePage
                    }
            }
            
            Section(header: Text("快速设置")) {
                Button(action: {
                    homePageText = "https://www.baidu.com"
                    settingsManager.homePage = homePageText
                }) {
                    HStack {
                        Text("百度首页")
                        Spacer()
                        if settingsManager.homePage == "https://www.baidu.com" {
                            Image(systemName: "checkmark")
                                .foregroundColor(.blue)
                        }
                    }
                }
                
                Button(action: {
                    homePageText = "about:blank"
                    settingsManager.homePage = homePageText
                }) {
                    HStack {
                        Text("空白页")
                        Spacer()
                        if settingsManager.homePage == "about:blank" {
                            Image(systemName: "checkmark")
                                .foregroundColor(.blue)
                        }
                    }
                }
            }
        }
        .navigationTitle("首页设置")
        .navigationBarTitleDisplayMode(.inline)
        .onDisappear {
            if !homePageText.isEmpty {
                settingsManager.homePage = homePageText
            }
        }
    }
}

struct PrivacyInfoView: View {
    var body: some View {
        List {
            Section(header: Text("隐私保护")) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("无痕浏览模式")
                        .font(.headline)
                    Text("开启无痕浏览后，您的浏览历史、搜索记录和表单数据将不会被保存。关闭所有无痕标签页后，相关数据将被清除。")
                        .font(.body)
                        .foregroundColor(.gray)
                }
                .padding(.vertical, 8)
            }
            
            Section(header: Text("数据存储")) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("本地存储")
                        .font(.headline)
                    Text("书签、历史记录和设置仅保存在您的设备本地，不会上传到任何服务器。")
                        .font(.body)
                        .foregroundColor(.gray)
                }
                .padding(.vertical, 8)
            }
            
            Section(header: Text("清除数据")) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("数据清除")
                        .font(.headline)
                    Text("您可以随时在设置中清除缓存、Cookie和浏览历史记录。")
                        .font(.body)
                        .foregroundColor(.gray)
                }
                .padding(.vertical, 8)
            }
        }
        .navigationTitle("隐私说明")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct AboutView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // App 图标
                ZStack {
                    RoundedRectangle(cornerRadius: 20)
                        .fill(LinearGradient(colors: [.blue, .purple], startPoint: .topLeading, endPoint: .bottomTrailing))
                        .frame(width: 80, height: 80)
                    
                    Image(systemName: "globe")
                        .font(.system(size: 40))
                        .foregroundColor(.white)
                }
                
                Text("初眠浏览器")
                    .font(.title)
                    .fontWeight(.bold)
                
                Text("版本 1.0.0")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                
                List {
                    Section(header: Text("功能特色")) {
                        FeatureRow(icon: "bolt", title: "极速浏览", description: "优化的渲染引擎，带来流畅的浏览体验")
                        FeatureRow(icon: "lock.shield", title: "安全保护", description: "HTTPS 加密，保护您的隐私安全")
                        FeatureRow(icon: "bookmark", title: "书签管理", description: "轻松管理您的收藏网站")
                        FeatureRow(icon: "eye.slash", title: "无痕浏览", description: "不留下任何浏览痕迹")
                    }
                    
                    Section(header: Text("开发者")) {
                        HStack {
                            Text("初眠科技")
                            Spacer()
                            Text("Chumian Tech")
                                .foregroundColor(.gray)
                        }
                    }
                    
                    Section(header: Text("开源协议")) {
                        Text("MIT License")
                            .foregroundColor(.gray)
                    }
                }
                .listStyle(InsetGroupedListStyle())
            }
            .padding(.top, 40)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("关闭") {
                        // 关闭
                    }
                }
            }
        }
    }
}

struct FeatureRow: View {
    let icon: String
    let title: String
    let description: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(.blue)
                .frame(width: 30)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.system(size: 15))
                Text(description)
                    .font(.system(size: 12))
                    .foregroundColor(.gray)
            }
        }
        .padding(.vertical, 4)
    }
}
