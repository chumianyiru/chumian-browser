package com.chumian.browser

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.chumian.browser.ui.screens.*
import com.chumian.browser.ui.theme.ChumianBrowserTheme

class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChumianBrowserTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrowserScreen()
                }
            }
        }

        requestNecessaryPermissions()
    }

    private fun requestNecessaryPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }
}

sealed class Screen {
    object Browser : Screen()
    object Bookmarks : Screen()
    object History : Screen()
    object Downloads : Screen()
    object Settings : Screen()
    object ViewSource : Screen()
    object SecurityInfo : Screen()
}

@Composable
fun BrowserScreen() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Browser) }
    var url by remember { mutableStateOf("https://www.baidu.com") }
    var currentUrl by remember { mutableStateOf("https://www.baidu.com") }
    var currentTitle by remember { mutableStateOf("百度一下") }
    var isLoading by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val bookmarkManager = ChumianApp.instance.bookmarkManager
    val historyManager = ChumianApp.instance.historyManager
    val downloadManager = ChumianApp.instance.downloadManager
    val settingsManager = ChumianApp.instance.settingsManager
    var isBookmarked by remember { mutableStateOf(false) }
    var sourceCode by remember { mutableStateOf("") }
    val isSecure = currentUrl.startsWith("https://")
    var showFindBar by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }
    var isIncognito by remember { mutableStateOf(false) }
    var isDesktopMode by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    LaunchedEffect(currentUrl) {
        isBookmarked = bookmarkManager.isBookmarked(currentUrl)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (currentScreen is Screen.Browser) {
            // 无痕模式指示器
            if (isIncognito) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "无痕浏览模式",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // 地址栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        IconButton(onClick = { currentScreen = Screen.SecurityInfo }) {
                            Icon(
                                imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "安全信息",
                                tint = if (isSecure) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    },
                    trailingIcon = {
                        if (isLoading) {
                            IconButton(onClick = { webView?.stopLoading() }) {
                                Icon(Icons.Default.Close, contentDescription = "停止")
                            }
                        } else {
                            IconButton(onClick = { webView?.reload() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "刷新")
                            }
                        }
                    }
                )
                IconButton(
                    onClick = {
                        if (url.isNotEmpty()) {
                            val searchUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                                url
                            } else {
                                settingsManager.getSearchUrl(url)
                            }
                            webView?.loadUrl(searchUrl)
                        }
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "前往")
                }
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "菜单")
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("前进") },
                            onClick = {
                                webView?.goForward()
                                showMenu = false
                            },
                            enabled = canGoForward
                        )
                        DropdownMenuItem(
                            text = { Text("后退") },
                            onClick = {
                                webView?.goBack()
                                showMenu = false
                            },
                            enabled = canGoBack
                        )
                        DropdownMenuItem(
                            text = { Text("首页") },
                            onClick = {
                                webView?.loadUrl("https://www.baidu.com")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isBookmarked) "取消书签" else "添加书签") },
                            onClick = {
                                if (isBookmarked) {
                                    bookmarkManager.removeBookmark(currentUrl)
                                } else {
                                    bookmarkManager.addBookmark(currentUrl, currentTitle)
                                }
                                isBookmarked = !isBookmarked
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("查看源码") },
                            onClick = {
                                showMenu = false
                                sourceCode = ""
                                currentScreen = Screen.ViewSource
                                // 获取网页源码
                                webView?.evaluateJavascript(
                                    "document.documentElement.outerHTML"
                                ) { result ->
                                    sourceCode = result?.trim('"')?.replace("\\n", "\n")?.replace("\\t", "\t") ?: ""
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isDesktopMode) "移动版页面" else "桌面版页面") },
                            onClick = {
                                showMenu = false
                                isDesktopMode = !isDesktopMode
                                webView?.settings?.userAgentString = if (isDesktopMode) {
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                } else {
                                    null
                                }
                                webView?.settings?.useWideViewPort = isDesktopMode
                                webView?.settings?.loadWithOverviewMode = isDesktopMode
                                webView?.reload()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isIncognito) "退出无痕模式" else "无痕浏览") },
                            onClick = {
                                showMenu = false
                                isIncognito = !isIncognito
                                if (isIncognito) {
                                    // 进入无痕模式时清除历史记录状态
                                    webView?.clearHistory()
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("页面内查找") },
                            onClick = {
                                showMenu = false
                                showFindBar = !showFindBar
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("分享") },
                            onClick = {
                                showMenu = false
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TITLE, currentTitle)
                                    putExtra(Intent.EXTRA_TEXT, "$currentTitle\n$currentUrl")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "分享网页"))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isFullscreen) "退出全屏" else "全屏浏览") },
                            onClick = {
                                showMenu = false
                                isFullscreen = !isFullscreen
                                val activity = context as? android.app.Activity
                                if (isFullscreen) {
                                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                                } else {
                                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("清除缓存") },
                            onClick = {
                                showMenu = false
                                webView?.clearCache(true)
                                CookieManager.getInstance().removeAllCookies(null)
                            }
                        )
                    }
                }
            }

            // 加载进度条
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 查找栏
            if (showFindBar) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = findQuery,
                        onValueChange = {
                            findQuery = it
                            webView?.findAllAsync(it)
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("在页面中查找") }
                    )
                    IconButton(onClick = { webView?.findNext(false) }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "上一个")
                    }
                    IconButton(onClick = { webView?.findNext(true) }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "下一个")
                    }
                    IconButton(onClick = {
                        showFindBar = false
                        findQuery = ""
                        webView?.clearMatches()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
            }

            // WebView
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = { context ->
                        try {
                            WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, pageUrl: String?, favicon: android.graphics.Bitmap?) {
                                    isLoading = true
                                    pageUrl?.let {
                                        currentUrl = it
                                        url = it
                                    }
                                    canGoBack = view?.canGoBack() ?: false
                                    canGoForward = view?.canGoForward() ?: false
                                }

                                override fun onPageFinished(view: WebView?, pageUrl: String?) {
                                    isLoading = false
                                    canGoBack = view?.canGoBack() ?: false
                                    canGoForward = view?.canGoForward() ?: false
                                    currentTitle = view?.title ?: ""
                                    // 添加到历史记录（无痕模式下不记录）
                                    if (!isIncognito) {
                                        pageUrl?.let {
                                            try {
                                                historyManager.addHistory(it, currentTitle)
                                            } catch (e: Exception) {
                                                // 忽略历史记录异常
                                            }
                                        }
                                    }
                                }
                            }

                            // 下载监听
                            setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                                    try {
                                        downloadManager.startDownload(url, userAgent)
                                    } catch (e: Exception) {
                                        // 忽略下载异常
                                    }
                            })

                                try {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.databaseEnabled = true
                                    settings.setSupportZoom(true)
                                    settings.builtInZoomControls = true
                                    settings.displayZoomControls = false
                                    settings.loadWithOverviewMode = true
                                    settings.useWideViewPort = true
                                    settings.javaScriptCanOpenWindowsAutomatically = true
                                    settings.setSupportMultipleWindows(true)
                                    settings.allowFileAccess = true
                                    settings.allowContentAccess = true
                                    settings.mediaPlaybackRequiresUserGesture = false
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    }
                                } catch (e: Exception) {
                                    // 忽略WebView设置异常
                                }
                                loadUrl("https://www.baidu.com")
                                webView = this
                            }
                        } catch (e: Exception) {
                            android.view.View(context)
                        }
                    },
                    update = { view ->
                        webView = view as? WebView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else if (currentScreen is Screen.Bookmarks) {
            BookmarksScreen(
                bookmarkManager = bookmarkManager,
                onBookmarkClick = { bookmarkUrl ->
                    webView?.loadUrl(bookmarkUrl)
                    currentScreen = Screen.Browser
                },
                onBack = { currentScreen = Screen.Browser }
            )
        } else if (currentScreen is Screen.History) {
            HistoryScreen(
                historyManager = historyManager,
                onHistoryClick = { historyUrl ->
                    webView?.loadUrl(historyUrl)
                    currentScreen = Screen.Browser
                },
                onBack = { currentScreen = Screen.Browser }
            )
        } else if (currentScreen is Screen.Downloads) {
            DownloadsScreen(
                onBack = { currentScreen = Screen.Browser }
            )
        } else if (currentScreen is Screen.Settings) {
            SettingsScreen(
                settingsManager = settingsManager,
                onBack = { currentScreen = Screen.Browser }
            )
        } else if (currentScreen is Screen.ViewSource) {
            ViewSourceScreen(
                sourceCode = sourceCode,
                onBack = { currentScreen = Screen.Browser }
            )
        } else if (currentScreen is Screen.SecurityInfo) {
            SecurityInfoScreen(
                url = currentUrl,
                isSecure = isSecure,
                onBack = { currentScreen = Screen.Browser }
            )
        }

        // 底部导航栏
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                label = { Text("首页") },
                selected = currentScreen is Screen.Browser,
                onClick = {
                    currentScreen = Screen.Browser
                    webView?.loadUrl("https://www.baidu.com")
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "书签") },
                label = { Text("书签") },
                selected = currentScreen is Screen.Bookmarks,
                onClick = { currentScreen = Screen.Bookmarks }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.History, contentDescription = "历史") },
                label = { Text("历史") },
                selected = currentScreen is Screen.History,
                onClick = { currentScreen = Screen.History }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Download, contentDescription = "下载") },
                label = { Text("下载") },
                selected = currentScreen is Screen.Downloads,
                onClick = { currentScreen = Screen.Downloads }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                label = { Text("设置") },
                selected = currentScreen is Screen.Settings,
                onClick = { currentScreen = Screen.Settings }
            )
        }
    }
}
