package com.chumian.browser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.chumian.browser.viewmodel.MainViewModel

@Composable
fun ViewSourceScreen(mainViewModel: MainViewModel) {
    val sourceCode by mainViewModel.sourceCode.collectAsState()
    val currentUrl by mainViewModel.currentUrl.collectAsState()
    
    var lineWrap by remember { mutableStateOf(true) }
    var showLineNumbers by remember { mutableStateOf(true) }
    var syntaxHighlight by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("查看源码") },
                actions = {
                    IconButton(onClick = { lineWrap = !lineWrap }) {
                        Icon(
                            imageVector = if (lineWrap) Icons.Default.WrapText else Icons.Default.TextWrap,
                            contentDescription = "自动换行"
                        )
                    }
                    IconButton(onClick = { showLineNumbers = !showLineNumbers }) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = "行号")
                    }
                    IconButton(onClick = { /* 复制 */ }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "复制")
                    }
                    IconButton(onClick = { /* 刷新 */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // URL信息
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            
            // 源码内容
            if (sourceCode.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "正在加载源码...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 行号
                    if (showLineNumbers) {
                        val lines = sourceCode.lines()
                        Column(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        ) {
                            lines.forEachIndexed { index, _ ->
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        VerticalDivider()
                    }
                    
                    // 代码
                    Text(
                        text = sourceCode,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
