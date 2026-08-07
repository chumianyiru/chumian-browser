package com.chumian.browser

import android.Manifest
import android.os.Build
import android.os.Bundle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.chumian.browser.ui.screens.*
import com.chumian.browser.ui.theme.ChumianBrowserTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val bookmarkManager = ChumianApp.instance.bookmarkManager
    val historyManager = ChumianApp.instance.historyManager
    val downloadManager = ChumianApp.instance.downloadManager
    var isBookmarked by remember { mutableStateOf(false) }
    var sourceCode by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUrl) {
        isBookmarked = bookmarkManager.isBookmarked(currentUrl)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (currentScreen is Screen.Browser) {
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
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                                "https://www.baidu.com/s?wd=$url"
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
                            text = { Text("分享") },
                            onClick = {
                                showMenu = false
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

            // WebView
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = { context ->
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
                                    // 添加到历史记录
                                    pageUrl?.let {
                                        historyManager.addHistory(it, currentTitle)
                                    }
                                }
                            }

                            // 下载监听
                            setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                                downloadManager.startDownload(url, userAgent)
                            })

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
                            loadUrl("https://www.baidu.com")
                            webView = this
                        }
                    },
                    update = { view ->
                        webView = view
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
                onBack = { currentScreen = Screen.Browser }
            )
        } else if (currentScreen is Screen.ViewSource) {
            ViewSourceScreen(
                sourceCode = sourceCode,
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
