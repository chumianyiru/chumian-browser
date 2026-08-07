import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var settingsManager: SettingsManager
    @State private var showSearchEngineDialog = false
    @State private var showThemeDialog = false
    @State private var showFontSizeDialog = false
    @State private var showClearDataDialog = false
    
    var body: some View {
        NavigationView {
            List {
                Section {
                    Button(action: { showSearchEngineDialog = true }) {
                        HStack {
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            VStack(alignment: .leading) {
                                Text("搜索引擎")
                                Text(settingsManager.getSearchEngineName())
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                    
                    Button(action: { showThemeDialog = true }) {
                        HStack {
                            Image(systemName: "paintpalette")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            VStack(alignment: .leading) {
                                Text("主题")
                                Text(settingsManager.getThemeName())
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                    
                    Button(action: { showFontSizeDialog = true }) {
                        HStack {
                            Image(systemName: "textformat.size")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            VStack(alignment: .leading) {
                                Text("字体大小")
                                Text(settingsManager.getFontSizeName())
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                }
                
                Section {
                    Button(action: { showClearDataDialog = true }) {
                        HStack {
                            Image(systemName: "shield")
                                .foregroundColor(.blue)
                                .frame(width: 30)
                            VStack(alignment: .leading) {
                                Text("隐私设置")
                                Text("清除缓存、Cookie等")
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                }
                
                Section {
                    HStack {
                        Image(systemName: "info.circle")
                            .foregroundColor(.blue)
                            .frame(width: 30)
                        VStack(alignment: .leading) {
                            Text("关于")
                            Text("初眠浏览器 v1.0.0")
                                .font(.caption)
                                .foregroundColor(.gray)
                        }
                    }
                }
            }
            .navigationTitle("设置")
            .navigationBarTitleDisplayMode(.inline)
            .confirmationDialog("选择搜索引擎", isPresented: $showSearchEngineDialog, titleVisibility: .visible) {
                Button("百度") { settingsManager.searchEngine = "baidu" }
                Button("谷歌") { settingsManager.searchEngine = "google" }
                Button("必应") { settingsManager.searchEngine = "bing" }
                Button("搜狗") { settingsManager.searchEngine = "sogou" }
                Button("360搜索") { settingsManager.searchEngine = "360" }
                Button("取消", role: .cancel) { }
            }
            .confirmationDialog("选择主题", isPresented: $showThemeDialog, titleVisibility: .visible) {
                Button("跟随系统") { settingsManager.themeMode = "system" }
                Button("浅色") { settingsManager.themeMode = "light" }
                Button("深色") { settingsManager.themeMode = "dark" }
                Button("取消", role: .cancel) { }
            }
            .confirmationDialog("选择字体大小", isPresented: $showFontSizeDialog, titleVisibility: .visible) {
                Button("较小") { settingsManager.fontSize = 75 }
                Button("默认") { settingsManager.fontSize = 100 }
                Button("较大") { settingsManager.fontSize = 125 }
                Button("大") { settingsManager.fontSize = 150 }
                Button("取消", role: .cancel) { }
            }
            .confirmationDialog("隐私设置", isPresented: $showClearDataDialog, titleVisibility: .visible) {
                Button("清除缓存", role: .destructive) { }
                Button("清除Cookie", role: .destructive) { }
                Button("清除历史记录", role: .destructive) { }
                Button("取消", role: .cancel) { }
            } message: {
                Text("清除浏览数据：")
            }
        }
    }
}
