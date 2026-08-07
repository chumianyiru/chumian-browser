import Foundation
import SwiftUI

class SettingsManager: ObservableObject {
    @Published var searchEngine: String {
        didSet { UserDefaults.standard.set(searchEngine, forKey: "searchEngine") }
    }
    @Published var themeMode: String {
        didSet { UserDefaults.standard.set(themeMode, forKey: "themeMode") }
    }
    @Published var fontSize: Int {
        didSet { UserDefaults.standard.set(fontSize, forKey: "fontSize") }
    }
    
    init() {
        self.searchEngine = UserDefaults.standard.string(forKey: "searchEngine") ?? "baidu"
        self.themeMode = UserDefaults.standard.string(forKey: "themeMode") ?? "system"
        self.fontSize = UserDefaults.standard.integer(forKey: "fontSize")
        if self.fontSize == 0 { self.fontSize = 100 }
    }
    
    func getSearchUrl(_ query: String) -> String {
        let encodedQuery = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
        switch searchEngine {
        case "baidu":
            return "https://www.baidu.com/s?wd=\(encodedQuery)"
        case "google":
            return "https://www.google.com/search?q=\(encodedQuery)"
        case "bing":
            return "https://www.bing.com/search?q=\(encodedQuery)"
        case "sogou":
            return "https://www.sogou.com/web?query=\(encodedQuery)"
        case "360":
            return "https://www.so.com/s?q=\(encodedQuery)"
        default:
            return "https://www.baidu.com/s?wd=\(encodedQuery)"
        }
    }
    
    func getSearchEngineName() -> String {
        switch searchEngine {
        case "baidu": return "百度"
        case "google": return "谷歌"
        case "bing": return "必应"
        case "sogou": return "搜狗"
        case "360": return "360搜索"
        default: return "百度"
        }
    }
    
    func getThemeName() -> String {
        switch themeMode {
        case "light": return "浅色"
        case "dark": return "深色"
        case "system": return "跟随系统"
        default: return "跟随系统"
        }
    }
    
    func getFontSizeName() -> String {
        switch fontSize {
        case 75: return "较小"
        case 100: return "默认"
        case 125: return "较大"
        case 150: return "大"
        default: return "默认"
        }
    }
}
