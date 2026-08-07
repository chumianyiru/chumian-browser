import Foundation
import SwiftUI

enum SearchEngine: String, CaseIterable, Identifiable {
    case baidu = "baidu"
    case google = "google"
    case bing = "bing"
    case sogou = "sogou"
    case search360 = "360"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .baidu: return "百度"
        case .google: return "谷歌"
        case .bing: return "必应"
        case .sogou: return "搜狗"
        case .search360: return "360搜索"
        }
    }
    
    var searchUrl: String {
        switch self {
        case .baidu: return "https://www.baidu.com/s?wd="
        case .google: return "https://www.google.com/search?q="
        case .bing: return "https://www.bing.com/search?q="
        case .sogou: return "https://www.sogou.com/web?query="
        case .search360: return "https://www.so.com/s?q="
        }
    }
    
    var homeUrl: String {
        switch self {
        case .baidu: return "https://www.baidu.com"
        case .google: return "https://www.google.com"
        case .bing: return "https://www.bing.com"
        case .sogou: return "https://www.sogou.com"
        case .search360: return "https://www.so.com"
        }
    }
}

enum ThemeMode: String, CaseIterable, Identifiable {
    case system = "system"
    case light = "light"
    case dark = "dark"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .system: return "跟随系统"
        case .light: return "浅色"
        case .dark: return "深色"
        }
    }
    
    var colorScheme: ColorScheme? {
        switch self {
        case .system: return nil
        case .light: return .light
        case .dark: return .dark
        }
    }
}

class SettingsManager: ObservableObject {
    @Published var searchEngine: SearchEngine {
        didSet { UserDefaults.standard.set(searchEngine.rawValue, forKey: "searchEngine") }
    }
    
    @Published var themeMode: ThemeMode {
        didSet { UserDefaults.standard.set(themeMode.rawValue, forKey: "themeMode") }
    }
    
    @Published var fontSize: Int {
        didSet { UserDefaults.standard.set(fontSize, forKey: "fontSize") }
    }
    
    @Published var isDesktopMode: Bool {
        didSet { UserDefaults.standard.set(isDesktopMode, forKey: "isDesktopMode") }
    }
    
    @Published var homePage: String {
        didSet { UserDefaults.standard.set(homePage, forKey: "homePage") }
    }
    
    @Published var blockPopups: Bool {
        didSet { UserDefaults.standard.set(blockPopups, forKey: "blockPopups") }
    }
    
    @Published var saveHistory: Bool {
        didSet { UserDefaults.standard.set(saveHistory, forKey: "saveHistory") }
    }
    
    init() {
        let savedEngine = UserDefaults.standard.string(forKey: "searchEngine") ?? "baidu"
        self.searchEngine = SearchEngine(rawValue: savedEngine) ?? .baidu
        
        let savedTheme = UserDefaults.standard.string(forKey: "themeMode") ?? "system"
        self.themeMode = ThemeMode(rawValue: savedTheme) ?? .system
        
        let savedFontSize = UserDefaults.standard.integer(forKey: "fontSize")
        self.fontSize = savedFontSize > 0 ? savedFontSize : 100
        
        self.isDesktopMode = UserDefaults.standard.bool(forKey: "isDesktopMode")
        
        self.homePage = UserDefaults.standard.string(forKey: "homePage") ?? "https://www.baidu.com"
        
        self.blockPopups = UserDefaults.standard.object(forKey: "blockPopups") as? Bool ?? true
        
        self.saveHistory = UserDefaults.standard.object(forKey: "saveHistory") as? Bool ?? true
    }
    
    func getSearchUrl(_ query: String) -> String {
        let encodedQuery = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
        return searchEngine.searchUrl + encodedQuery
    }
    
    var desktopUserAgent: String {
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15"
    }
    
    var mobileUserAgent: String {
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1"
    }
    
    func currentUserAgent(isDesktop: Bool) -> String {
        isDesktop ? desktopUserAgent : mobileUserAgent
    }
}
