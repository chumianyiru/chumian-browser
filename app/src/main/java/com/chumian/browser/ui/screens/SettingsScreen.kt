package com.chumian.browser.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun SettingsScreen(
    mainViewModel: MainViewModel,
    navController: NavController? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
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
            // 常规设置
            item {
                SettingsGroup(title = "常规") {
                    SettingsItem(
                        icon = Icons.Default.Search,
                        title = "搜索引擎",
                        subtitle = "百度",
                        onClick = { navController?.navigate("search_engine_settings") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Home,
                        title = "主页",
                        subtitle = "初始页",
                        onClick = { /* 主页设置 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Download,
                        title = "下载设置",
                        subtitle = "Download/ChumianBrowser",
                        onClick = { navController?.navigate("download_settings") }
                    )
                }
            }
            
            // 外观设置
            item {
                SettingsGroup(title = "外观") {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "主题",
                        subtitle = "跟随系统",
                        onClick = { navController?.navigate("theme_settings") }
                    )
                    SettingsItem(
                        icon = Icons.Default.TextFields,
                        title = "字体大小",
                        subtitle = "100%",
                        onClick = { /* 字体大小设置 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Brightness6,
                        title = "夜间模式",
                        subtitle = "关闭",
                        onClick = { /* 夜间模式设置 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Dock,
                        title = "底部导航栏",
                        subtitle = "开启",
                        onClick = { /* 导航栏设置 */ }
                    )
                }
            }
            
            // 隐私与安全
            item {
                SettingsGroup(title = "隐私与安全") {
                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "安全设置",
                        subtitle = "安全浏览已开启",
                        onClick = { navController?.navigate("security_settings") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "密码管理",
                        subtitle = "记住密码已开启",
                        onClick = { navController?.navigate("password_manager") }
                    )
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "隐私设置",
                        subtitle = "无痕模式",
                        onClick = { navController?.navigate("privacy_settings") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Block,
                        title = "广告拦截",
                        subtitle = "关闭",
                        onClick = { navController?.navigate("ad_block_settings") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Cookie,
                        title = "Cookie管理",
                        subtitle = "已接受",
                        onClick = { navController?.navigate("cookie_manager") }
                    )
                }
            }
            
            // 浏览设置
            item {
                SettingsGroup(title = "浏览") {
                    SettingsItem(
                        icon = Icons.Default.DesktopMac,
                        title = "桌面模式",
                        subtitle = "关闭",
                        onClick = { /* 桌面模式切换 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Javascript,
                        title = "JavaScript",
                        subtitle = "开启",
                        onClick = { /* JavaScript设置 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.ZoomIn,
                        title = "缩放",
                        subtitle = "100%",
                        onClick = { /* 缩放设置 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Translate,
                        title = "网页翻译",
                        subtitle = "自动翻译",
                        onClick = { /* 翻译设置 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.ChromeReaderMode,
                        title = "阅读模式",
                        subtitle = "自动检测",
                        onClick = { /* 阅读模式设置 */ }
                    )
                }
            }
            
            // 高级设置
            item {
                SettingsGroup(title = "高级") {
                    SettingsItem(
                        icon = Icons.Default.Code,
                        title = "开发者工具",
                        subtitle = "开启",
                        onClick = { navController?.navigate("devtools") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Extension,
                        title = "模块管理",
                        subtitle = "1个已安装",
                        onClick = { navController?.navigate("module_manager") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Settings,
                        title = "站点设置",
                        subtitle = "权限管理",
                        onClick = { navController?.navigate("site_settings") }
                    )
                    SettingsItem(
                        icon = Icons.Default.DataUsage,
                        title = "数据使用",
                        subtitle = "流量统计",
                        onClick = { navController?.navigate("data_usage") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Backup,
                        title = "备份与恢复",
                        subtitle = "书签、历史记录",
                        onClick = { navController?.navigate("backup_restore") }
                    )
                }
            }
            
            // 关于
            item {
                SettingsGroup(title = "关于") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "关于初眠浏览器",
                        subtitle = "版本 1.0.0",
                        onClick = { navController?.navigate("about") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "帮助与反馈",
                        subtitle = "常见问题",
                        onClick = { /* 帮助 */ }
                    )
                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "开源许可",
                        subtitle = "MIT License",
                        onClick = { /* 开源许可 */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
