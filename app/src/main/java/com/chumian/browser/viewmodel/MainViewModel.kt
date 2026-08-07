package com.chumian.browser.viewmodel

import android.app.Application
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chumian.browser.ChumianApp
import com.chumian.browser.data.model.Bookmark
import com.chumian.browser.data.model.DownloadItem
import com.chumian.browser.data.model.HistoryItem
import com.chumian.browser.data.model.Tab
import com.chumian.browser.data.repository.SettingsRepository
import com.chumian.browser.module.ModuleManager
import com.chumian.browser.security.SecurityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as ChumianApp
    private val settingsRepository: SettingsRepository = app.settingsRepository
    private val moduleManager: ModuleManager = app.moduleManager
    private val securityManager: SecurityManager = app.securityManager
    
    // 标签页
    private val _tabs = MutableLiveData<MutableList<Tab>>(mutableListOf())
    val tabs: LiveData<MutableList<Tab>> = _tabs
    
    private val _currentTabIndex = MutableLiveData(0)
    val currentTabIndex: LiveData<Int> = _currentTabIndex
    
    // 当前URL
    private val _currentUrl = MutableLiveData("https://www.baidu.com")
    val currentUrl: LiveData<String> = _currentUrl
    
    // 当前标题
    private val _currentTitle = MutableLiveData("初始页")
    val currentTitle: LiveData<String> = _currentTitle
    
    // 加载进度
    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress
    
    // 是否正在加载
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 是否可以后退
    private val _canGoBack = MutableLiveData(false)
    val canGoBack: LiveData<Boolean> = _canGoBack
    
    // 是否可以前进
    private val _canGoForward = MutableLiveData(false)
    val canGoForward: LiveData<Boolean> = _canGoForward
    
    // 是否为桌面模式
    private val _desktopMode = MutableLiveData(false)
    val desktopMode: LiveData<Boolean> = _desktopMode
    
    // 是否为夜间模式
    private val _nightMode = MutableLiveData(false)
    val nightMode: LiveData<Boolean> = _nightMode
    
    // 是否为无痕模式
    private val _incognitoMode = MutableLiveData(false)
    val incognitoMode: LiveData<Boolean> = _incognitoMode
    
    // 字体大小
    private val _fontSize = MutableLiveData(100)
    val fontSize: LiveData<Int> = _fontSize
    
    // 当前搜索引擎
    private val _searchEngine = MutableLiveData("baidu")
    val searchEngine: LiveData<String> = _searchEngine
    
    // 书签列表
    private val _bookmarks = MutableLiveData<List<Bookmark>>(emptyList())
    val bookmarks: LiveData<List<Bookmark>> = _bookmarks
    
    // 历史记录
    private val _history = MutableLiveData<List<HistoryItem>>(emptyList())
    val history: LiveData<List<HistoryItem>> = _history
    
    // 下载列表
    private val _downloads = MutableLiveData<List<DownloadItem>>(emptyList())
    val downloads: LiveData<List<DownloadItem>> = _downloads
    
    // 开发者工具相关
    private val _viewSource = MutableLiveData("")
    val viewSource: LiveData<String> = _viewSource
    
    private val _consoleLogs = MutableLiveData<List<String>>(emptyList())
    val consoleLogs: LiveData<List<String>> = _consoleLogs
    
    private val _networkRequests = MutableLiveData<List<Map<String, String>>>(emptyList())
    val networkRequests: LiveData<List<Map<String, String>>> = _networkRequests
    
    // 安全信息
    private val _securityInfo = MutableLiveData<Map<String, String>>(emptyMap())
    val securityInfo: LiveData<Map<String, String>> = _securityInfo
    
    private val _certificateInfo = MutableLiveData<Map<String, String>>(emptyMap())
    val certificateInfo: LiveData<Map<String, String>> = _certificateInfo
    
    private val _securityRating = MutableLiveData("A")
    val securityRating: LiveData<String> = _securityRating
    
    // WebView引用
    private var currentWebView: WebView? = null
    
    init {
        loadSettings()
        loadInitialData()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _searchEngine.value = settingsRepository.getSearchEngine()
            _desktopMode.value = settingsRepository.getDesktopMode()
            _nightMode.value = settingsRepository.getNightMode()
            _fontSize.value = settingsRepository.getFontSize()
        }
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            loadBookmarks()
            loadHistory()
            loadDownloads()
        }
    }
    
    // 导航操作
    fun loadUrl(url: String) {
        val processedUrl = if (url.contains(".")) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
        } else {
            search(url)
        }
        _currentUrl.value = processedUrl
    }
    
    fun search(query: String): String {
        val searchUrl = when (_searchEngine.value) {
            "baidu" -> "https://www.baidu.com/s?wd=$query"
            "bing" -> "https://www.bing.com/search?q=$query"
            "google" -> "https://www.google.com/search?q=$query"
            "sogou" -> "https://www.sogou.com/web?query=$query"
            "360" -> "https://www.so.com/s?q=$query"
            "duckduckgo" -> "https://duckduckgo.com/?q=$query"
            "yandex" -> "https://yandex.com/search/?text=$query"
            "yahoo" -> "https://search.yahoo.com/search?p=$query"
            else -> "https://www.baidu.com/s?wd=$query"
        }
        return searchUrl
    }
    
    fun goBack() {
        currentWebView?.goBack()
    }
    
    fun goForward() {
        currentWebView?.goForward()
    }
    
    fun refresh() {
        currentWebView?.reload()
    }
    
    fun stopLoading() {
        currentWebView?.stopLoading()
    }
    
    // 标签页管理
    fun newTab(url: String = "https://www.baidu.com") {
        val tabs = _tabs.value ?: mutableListOf()
        val newTab = Tab(
            id = System.currentTimeMillis(),
            url = url,
            title = "新标签页",
            isActive = false
        )
        tabs.add(newTab)
        _tabs.value = tabs
        switchTab(tabs.size - 1)
    }
    
    fun closeTab(index: Int) {
        val tabs = _tabs.value ?: mutableListOf()
        if (tabs.size > 1 && index in tabs.indices) {
            tabs.removeAt(index)
            _tabs.value = tabs
            if (_currentTabIndex.value ?: 0 >= index) {
                _currentTabIndex.value = (_currentTabIndex.value ?: 0) - 1
            }
        }
    }
    
    fun switchTab(index: Int) {
        val tabs = _tabs.value ?: mutableListOf()
        if (index in tabs.indices) {
            tabs.forEachIndexed { i, tab ->
                tab.isActive = i == index
            }
            _tabs.value = tabs
            _currentTabIndex.value = index
            _currentUrl.value = tabs[index].url
            _currentTitle.value = tabs[index].title
        }
    }
    
    // 设置WebView
    fun setWebView(webView: WebView) {
        currentWebView = webView
    }
    
    // 更新加载状态
    fun updateProgress(progress: Int) {
        _progress.value = progress
        _isLoading.value = progress in 1..99
    }
    
    fun updateCanGoBack(canGoBack: Boolean) {
        _canGoBack.value = canGoBack
    }
    
    fun updateCanGoForward(canGoForward: Boolean) {
        _canGoForward.value = canGoForward
    }
    
    fun updateTitle(title: String) {
        _currentTitle.value = title
        val tabs = _tabs.value ?: mutableListOf()
        val index = _currentTabIndex.value ?: 0
        if (index in tabs.indices) {
            tabs[index].title = title
            _tabs.value = tabs
        }
    }
    
    fun updateUrl(url: String) {
        _currentUrl.value = url
        val tabs = _tabs.value ?: mutableListOf()
        val index = _currentTabIndex.value ?: 0
        if (index in tabs.indices) {
            tabs[index].url = url
            _tabs.value = tabs
        }
        
        // 添加到历史记录
        addToHistory(url, _currentTitle.value ?: "")
    }
    
    // 书签
    fun loadBookmarks() {
        viewModelScope.launch {
            val bookmarks = app.bookmarkManager.getAllBookmarks()
            _bookmarks.postValue(bookmarks)
        }
    }
    
    fun addBookmark(url: String, title: String) {
        viewModelScope.launch {
            app.bookmarkManager.addBookmark(url, title)
            loadBookmarks()
        }
    }
    
    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            app.bookmarkManager.deleteBookmark(id)
            loadBookmarks()
        }
    }
    
    fun isBookmarked(url: String): Boolean {
        return _bookmarks.value?.any { it.url == url } ?: false
    }
    
    // 历史记录
    fun loadHistory() {
        viewModelScope.launch {
            val history = app.historyManager.getAllHistory()
            _history.postValue(history)
        }
    }
    
    fun addToHistory(url: String, title: String) {
        if (!_incognitoMode.value!!) {
            viewModelScope.launch {
                app.historyManager.addHistory(url, title)
                loadHistory()
            }
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            app.historyManager.clearHistory()
            loadHistory()
        }
    }
    
    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            app.historyManager.deleteHistory(id)
            loadHistory()
        }
    }
    
    // 下载
    fun loadDownloads() {
        viewModelScope.launch {
            val downloads = app.downloadManager.getAllDownloads()
            _downloads.postValue(downloads)
        }
    }
    
    // 开发者工具
    fun loadViewSource() {
        viewModelScope.launch {
            currentWebView?.evaluateJavascript(
                "document.documentElement.outerHTML"
            ) { result ->
                _viewSource.postValue(result?.removeSurrounding("\"")?.replace("\\n", "\n") ?: "")
            }
        }
    }
    
    fun addConsoleLog(message: String) {
        val logs = _consoleLogs.value?.toMutableList() ?: mutableListOf()
        logs.add(message)
        _consoleLogs.value = logs
    }
    
    fun clearConsole() {
        _consoleLogs.value = emptyList()
    }
    
    // 安全功能
    fun checkSecurity(url: String) {
        viewModelScope.launch {
            val info = securityManager.checkWebsiteSecurity(url)
            _securityInfo.postValue(info)
            _securityRating.postValue(info["rating"] ?: "A")
        }
    }
    
    fun loadCertificateInfo(url: String) {
        viewModelScope.launch {
            val certInfo = securityManager.getCertificateInfo(url)
            _certificateInfo.postValue(certInfo)
        }
    }
    
    // 设置
    fun setSearchEngine(engine: String) {
        _searchEngine.value = engine
        viewModelScope.launch {
            settingsRepository.setSearchEngine(engine)
        }
    }
    
    fun setDesktopMode(enabled: Boolean) {
        _desktopMode.value = enabled
        viewModelScope.launch {
            settingsRepository.setDesktopMode(enabled)
        }
    }
    
    fun setNightMode(enabled: Boolean) {
        _nightMode.value = enabled
        viewModelScope.launch {
            settingsRepository.setNightMode(enabled)
        }
    }
    
    fun setFontSize(size: Int) {
        _fontSize.value = size
        viewModelScope.launch {
            settingsRepository.setFontSize(size)
        }
    }
    
    fun setIncognitoMode(enabled: Boolean) {
        _incognitoMode.value = enabled
    }
    
    // 模块系统
    fun getInstalledModules() = moduleManager.getInstalledModules()
    
    fun getModuleById(id: String) = moduleManager.getModuleById(id)
    
    fun installModule(modulePath: String) = moduleManager.installModule(modulePath)
    
    fun uninstallModule(moduleId: String) = moduleManager.uninstallModule(moduleId)
    
    fun enableModule(moduleId: String) = moduleManager.enableModule(moduleId)
    
    fun disableModule(moduleId: String) = moduleManager.disableModule(moduleId)
    
    // 清理
    fun clearCache() {
        currentWebView?.clearCache(true)
    }
    
    fun clearCookies() {
        android.webkit.CookieManager.getInstance().removeAllCookies(null)
    }
    
    fun clearHistoryAndCache() {
        clearHistory()
        clearCache()
        clearCookies()
    }
}
