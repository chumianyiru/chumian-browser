package com.chumian.browser.ui.screens

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.chumian.browser.viewmodel.MainViewModel

@Composable
fun BrowserScreen(
    mainViewModel: MainViewModel,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val currentUrl by mainViewModel.currentUrl.collectAsState()
    val currentTitle by mainViewModel.currentTitle.collectAsState()
    val progress by mainViewModel.progress.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()
    val canGoBack by mainViewModel.canGoBack.collectAsState()
    val canGoForward by mainViewModel.canGoForward.collectAsState()
    val desktopMode by mainViewModel.desktopMode.collectAsState()
    val fontSize by mainViewModel.fontSize.collectAsState()
    
    var showUrlBar by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf(TextFieldValue("")) }
    var showMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 地址栏
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 安全图标
                IconButton(onClick = {
                    navController?.navigate("security_info")
                }) {
                    Icon(
                        imageVector = if (currentUrl.startsWith("https://")) {
                            Icons.Default.Lock
                        } else {
                            Icons.Default.LockOpen
                        },
                        contentDescription = "安全信息",
                        tint = if (currentUrl.startsWith("https://")) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
                
                // 地址栏
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (showUrlBar) {
                        TextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("搜索或输入网址") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    showUrlBar = false
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "取消")
                                }
                            }
                        )
                    } else {
                        TextButton(
                            onClick = {
                                urlInput = TextFieldValue(currentUrl)
                                showUrlBar = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentUrl,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // 刷新/停止按钮
                IconButton(onClick = {
                    if (isLoading) {
                        mainViewModel.stopLoading()
                    } else {
                        mainViewModel.refresh()
                    }
                }) {
                    Icon(
                        imageVector = if (isLoading) {
                            Icons.Default.Close
                        } else {
                            Icons.Default.Refresh
                        },
                        contentDescription = if (isLoading) "停止" else "刷新"
                    )
                }
                
                // 菜单按钮
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "菜单")
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("新标签页") },
                            onClick = {
                                mainViewModel.newTab()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("书签") },
                            onClick = {
                                navController?.navigate("bookmarks")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Bookmark, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("历史记录") },
                            onClick = {
                                navController?.navigate("history")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.History, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("下载") },
                            onClick = {
                                navController?.navigate("downloads")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Download, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("开发者工具") },
                            onClick = {
                                navController?.navigate("devtools")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Code, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("查看源码") },
                            onClick = {
                                mainViewModel.loadViewSource()
                                navController?.navigate("view_source")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Description, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("证书信息") },
                            onClick = {
                                mainViewModel.loadCertificateInfo(currentUrl)
                                navController?.navigate("certificate_details")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Verified, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("添加书签") },
                            onClick = {
                                mainViewModel.addBookmark(currentUrl, currentTitle)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("分享") },
                            onClick = {
                                navController?.navigate("share")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("查找") },
                            onClick = {
                                navController?.navigate("find_in_page")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("设置") },
                            onClick = {
                                navController?.navigate("settings")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
        
        // 进度条
        if (isLoading) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // WebView
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            cacheMode = WebSettings.LOAD_DEFAULT
                            allowFileAccess = true
                            allowContentAccess = true
                            mediaPlaybackRequiresUserGesture = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            textZoom = fontSize
                        }
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let { mainViewModel.updateUrl(it) }
                                view?.let {
                                    mainViewModel.updateCanGoBack(it.canGoBack())
                                    mainViewModel.updateCanGoForward(it.canGoForward())
                                }
                            }
                        }
                        
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                mainViewModel.updateProgress(newProgress)
                            }
                            
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                title?.let { mainViewModel.updateTitle(it) }
                            }
                        }
                        
                        mainViewModel.setWebView(this)
                        loadUrl(currentUrl)
                    }
                },
                update = { webView ->
                    webView.settings.textZoom = fontSize
                    webView.settings.userAgentString = if (desktopMode) {
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                    } else {
                        null
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // 底部导航栏
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.ArrowBack, contentDescription = "后退") },
                label = { Text("后退") },
                selected = false,
                enabled = canGoBack,
                onClick = { mainViewModel.goBack() }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.ArrowForward, contentDescription = "前进") },
                label = { Text("前进") },
                selected = false,
                enabled = canGoForward,
                onClick = { mainViewModel.goForward() }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                label = { Text("首页") },
                selected = false,
                onClick = {
                    mainViewModel.loadUrl("https://www.baidu.com")
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Tab, contentDescription = "标签页") },
                label = { Text("标签") },
                selected = false,
                onClick = {
                    navController?.navigate("tabs")
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Menu, contentDescription = "菜单") },
                label = { Text("菜单") },
                selected = false,
                onClick = { showMenu = true }
            )
        }
    }
}
