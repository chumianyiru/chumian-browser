import SwiftUI
import WebKit

struct WebView: UIViewRepresentable {
    @ObservedObject var tab: BrowserTab
    let settingsManager: SettingsManager
    let historyManager: HistoryManager
    let downloadManager: DownloadManager
    
    let isIncognito: Bool
    
    var onNavigationStart: (() -> Void)?
    var onNavigationFinish: ((String, String) -> Void)?
    var onUpdateProgress: ((Double) -> Void)?
    var onDownload: ((URL, String) -> Void)?
    
    func makeUIView(context: Context) -> WKWebView {
        let configuration = WKWebViewConfiguration()
        
        // 无痕模式配置
        if isIncognito {
            let dataStore = WKWebsiteDataStore.nonPersistent()
            configuration.websiteDataStore = dataStore
        }
        
        // 偏好设置
        let preferences = WKPreferences()
        preferences.javaScriptEnabled = true
        preferences.javaScriptCanOpenWindowsAutomatically = !settingsManager.blockPopups
        configuration.preferences = preferences
        
        // 用户内容控制器
        let userContentController = WKUserContentController()
        configuration.userContentController = userContentController
        
        // 创建 WebView
        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.navigationDelegate = context.coordinator
        webView.uiDelegate = context.coordinator
        webView.allowsBackForwardNavigationGestures = true
        webView.allowsLinkPreview = true
        webView.scrollView.bounces = true
        
        // 设置 User-Agent
        webView.customUserAgent = settingsManager.currentUserAgent(isDesktop: settingsManager.isDesktopMode)
        
        // 字体大小
        let fontSizeJS = "document.documentElement.style.webkitTextSizeAdjust = '\(settingsManager.fontSize)%';"
        let userScript = WKUserScript(source: fontSizeJS, injectionTime: .atDocumentEnd, forMainFrameOnly: true)
        userContentController.addUserScript(userScript)
        
        // 观察进度
        context.coordinator.progressObserver = webView.observe(\.estimatedProgress, options: .new) { _, change in
            if let progress = change.newValue {
                DispatchQueue.main.async {
                    onUpdateProgress?(progress)
                }
            }
        }
        
        // 观察标题
        context.coordinator.titleObserver = webView.observe(\.title, options: .new) { _, change in
            if let title = change.newValue ?? "" {
                DispatchQueue.main.async {
                    tab.title = title.isEmpty ? "新标签页" : title
                }
            }
        }
        
        // 观察 URL
        context.coordinator.urlObserver = webView.observe(\.url, options: .new) { _, change in
            if let url = change.newValue??.absoluteString {
                DispatchQueue.main.async {
                    tab.url = url
                }
            }
        }
        
        // 加载初始 URL
        if let url = URL(string: tab.url) {
            let request = URLRequest(url: url)
            webView.load(request)
        }
        
        tab.webView = webView
        return webView
    }
    
    func updateUIView(_ webView: WKWebView, context: Context) {
        // 更新 User-Agent
        webView.customUserAgent = settingsManager.currentUserAgent(isDesktop: settingsManager.isDesktopMode)
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, WKNavigationDelegate, WKUIDelegate {
        var parent: WebView
        var progressObserver: NSKeyValueObservation?
        var titleObserver: NSKeyValueObservation?
        var urlObserver: NSKeyValueObservation?
        
        init(_ parent: WebView) {
            self.parent = parent
            super.init()
        }
        
        deinit {
            progressObserver?.invalidate()
            titleObserver?.invalidate()
            urlObserver?.invalidate()
        }
        
        // MARK: - WKNavigationDelegate
        
        func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
            DispatchQueue.main.async {
                self.parent.tab.isLoading = true
                self.parent.onNavigationStart?()
            }
        }
        
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            DispatchQueue.main.async {
                self.parent.tab.isLoading = false
                self.parent.tab.canGoBack = webView.canGoBack
                self.parent.tab.canGoForward = webView.canGoForward
                
                let title = webView.title ?? "新标签页"
                let url = webView.url?.absoluteString ?? ""
                
                self.parent.tab.title = title
                self.parent.tab.url = url
                
                self.parent.onNavigationFinish?(title, url)
                
                // 添加到历史记录
                if !self.parent.isIncognito, !url.isEmpty, self.parent.settingsManager.saveHistory {
                    self.parent.historyManager.addHistory(url: url, title: title.isEmpty ? url : title)
                }
                
                // 应用字体大小
                let fontSizeJS = "document.documentElement.style.webkitTextSizeAdjust = '\(self.parent.settingsManager.fontSize)%';"
                webView.evaluateJavaScript(fontSizeJS, completionHandler: nil)
            }
        }
        
        func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
            DispatchQueue.main.async {
                self.parent.tab.isLoading = false
            }
        }
        
        func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
            DispatchQueue.main.async {
                self.parent.tab.isLoading = false
            }
        }
        
        func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
            // 检查是否是下载
            let mimeTypes = [
                "application/pdf",
                "application/zip",
                "application/octet-stream",
                "application/x-msdownload",
                "application/vnd.android.package-archive",
                "image/png",
                "image/jpeg",
                "image/gif"
            ]
            
            if let mimeType = navigationResponse.response.mimeType,
               mimeTypes.contains(mimeType),
               let url = navigationResponse.response.url {
                decisionHandler(.cancel)
                
                let fileName = url.lastPathComponent
                let fileSize = navigationResponse.response.expectedContentLength
                
                DispatchQueue.main.async {
                    self.parent.downloadManager.addDownload(url: url.absoluteString, fileName: fileName, fileSize: fileSize)
                    self.parent.onDownload?(url, fileName)
                }
                return
            }
            
            decisionHandler(.allow)
        }
        
        // MARK: - WKUIDelegate
        
        func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
            // 新窗口打开 - 在新标签页中打开
            if navigationAction.targetFrame == nil || navigationAction.targetFrame?.isMainFrame == false {
                if let url = navigationAction.request.url {
                    DispatchQueue.main.async {
                        NotificationCenter.default.post(name: .openNewTab, object: url.absoluteString)
                    }
                }
            }
            return nil
        }
        
        func webViewDidClose(_ webView: WKWebView) {
            // 窗口关闭
        }
        
        func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
            // JavaScript alert
            DispatchQueue.main.async {
                NotificationCenter.default.post(name: .showJSAlert, object: message)
                completionHandler()
            }
        }
        
        func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
            // JavaScript confirm
            DispatchQueue.main.async {
                NotificationCenter.default.post(name: .showJSConfirm, object: message)
                completionHandler(true)
            }
        }
        
        func webView(_ webView: WKWebView, runJavaScriptTextInputPanelWithPrompt prompt: String, defaultText: String?, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (String?) -> Void) {
            // JavaScript prompt
            DispatchQueue.main.async {
                NotificationCenter.default.post(name: .showJSPrompt, object: prompt)
                completionHandler(defaultText)
            }
        }
    }
}

extension Notification.Name {
    static let openNewTab = Notification.Name("openNewTab")
    static let showJSAlert = Notification.Name("showJSAlert")
    static let showJSConfirm = Notification.Name("showJSConfirm")
    static let showJSPrompt = Notification.Name("showJSPrompt")
}
