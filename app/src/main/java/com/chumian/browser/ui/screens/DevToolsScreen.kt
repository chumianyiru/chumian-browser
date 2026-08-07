package com.chumian.browser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chumian.browser.viewmodel.MainViewModel

@Composable
fun DevToolsScreen(
    mainViewModel: MainViewModel,
    navController: NavController? = null
) {
    val consoleLogs by mainViewModel.consoleLogs.collectAsState()
    
    val devToolsItems = listOf(
        DevToolItem(
            icon = Icons.Default.Code,
            title = "查看源码",
            description = "查看当前网页的HTML源代码",
            route = "view_source"
        ),
        DevToolItem(
            icon = Icons.Default.Terminal,
            title = "控制台",
            description = "JavaScript控制台和日志",
            route = "console"
        ),
        DevToolItem(
            icon = Icons.Default.DashboardCustomize,
            title = "元素审查",
            description = "检查和修改页面元素",
            route = "elements"
        ),
        DevToolItem(
            icon = Icons.Default.NetworkCheck,
            title = "网络监控",
            description = "查看网络请求和响应",
            route = "network"
        ),
        DevToolItem(
            icon = Icons.Default.Speed,
            title = "性能分析",
            description = "网页性能指标和分析",
            route = "performance"
        ),
        DevToolItem(
            icon = Icons.Default.Memory,
            title = "内存分析",
            description = "内存使用情况和泄漏检测",
            route = "memory"
        ),
        DevToolItem(
            icon = Icons.Default.Folder,
            title = "资源面板",
            description = "页面资源文件管理",
            route = "resources"
        ),
        DevToolItem(
            icon = Icons.Default.Source,
            title = "源代码面板",
            description = "调试JavaScript代码",
            route = "sources"
        ),
        DevToolItem(
            icon = Icons.Default.Apps,
            title = "应用面板",
            description = "PWA、存储、缓存等",
            route = "application"
        ),
        DevToolItem(
            icon = Icons.Default.Security,
            title = "安全面板",
            description = "安全信息和证书",
            route = "security_panel"
        ),
        DevToolItem(
            icon = Icons.Default.Lightbulb,
            title = "Lighthouse",
            description = "网页质量灯塔检测",
            route = "lighthouse"
        ),
        DevToolItem(
            icon = Icons.Default.Audit,
            title = "审计",
            description = "可访问性、SEO等审计",
            route = "audit"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("开发者工具") },
                actions = {
                    IconButton(onClick = { /* 远程调试 */ }) {
                        Icon(Icons.Default.Devices, contentDescription = "远程调试")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(devToolsItems) { item ->
                DevToolCard(
                    item = item,
                    onClick = { navController?.navigate(item.route) }
                )
            }
            
            // 控制台预览
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "控制台日志",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { navController?.navigate("console") }) {
                                Text("查看全部")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (consoleLogs.isEmpty()) {
                            Text(
                                text = "暂无日志",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            consoleLogs.take(5).forEach { log ->
                                Text(
                                    text = log,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DevToolItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val route: String
)

@Composable
fun DevToolCard(
    item: DevToolItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
