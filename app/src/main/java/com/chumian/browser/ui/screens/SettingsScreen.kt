package com.chumian.browser.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.chumian.browser.settings.SettingsManager

data class SettingItem(
    val title: String,
    val description: String? = null,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit
) {
    var showSearchEngineDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val searchEngineName = remember { settingsManager.getSearchEngineName() }
    val themeName = remember { settingsManager.getThemeName() }
    val fontSizeName = remember { settingsManager.getFontSizeName() }

    val settings = listOf(
        SettingItem(
            title = "搜索引擎",
            description = searchEngineName,
            icon = Icons.Default.Search,
            onClick = { showSearchEngineDialog = true }
        ),
        SettingItem(
            title = "主题",
            description = themeName,
            icon = Icons.Default.Palette,
            onClick = { showThemeDialog = true }
        ),
        SettingItem(
            title = "字体大小",
            description = fontSizeName,
            icon = Icons.Default.FormatSize,
            onClick = { showFontSizeDialog = true }
        ),
        SettingItem(
            title = "隐私设置",
            description = "清除缓存、Cookie等",
            icon = Icons.Default.Security,
            onClick = { showClearDataDialog = true }
        ),
        SettingItem(
            title = "关于",
            description = "初眠浏览器 v1.0.0",
            icon = Icons.Default.Info
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部栏
        TopAppBar(
            title = { Text("设置") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Settings, contentDescription = "返回")
                }
            }
        )

        // 设置列表
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(settings) { setting ->
                SettingItemRow(
                    item = setting,
                    onClick = setting.onClick
                )
                Divider()
            }
        }
    }

    // 搜索引擎选择对话框
    if (showSearchEngineDialog) {
        AlertDialog(
            onDismissRequest = { showSearchEngineDialog = false },
            title = { Text("选择搜索引擎") },
            text = {
                Column {
                    listOf(
                        "baidu" to "百度",
                        "google" to "Google",
                        "bing" to "必应",
                        "sogou" to "搜狗",
                        "360" to "360搜索"
                    ).forEach { (value, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsManager.searchEngine = value
                                    showSearchEngineDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settingsManager.searchEngine == value,
                                onClick = {
                                    settingsManager.searchEngine = value
                                    showSearchEngineDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchEngineDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 主题选择对话框
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("选择主题") },
            text = {
                Column {
                    listOf(
                        "system" to "跟随系统",
                        "light" to "浅色",
                        "dark" to "深色"
                    ).forEach { (value, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsManager.themeMode = value
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settingsManager.themeMode == value,
                                onClick = {
                                    settingsManager.themeMode = value
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 字体大小选择对话框
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = { Text("选择字体大小") },
            text = {
                Column {
                    listOf(
                        75 to "较小",
                        100 to "默认",
                        125 to "较大",
                        150 to "大"
                    ).forEach { (value, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsManager.fontSize = value
                                    showFontSizeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settingsManager.fontSize == value,
                                onClick = {
                                    settingsManager.fontSize = value
                                    showFontSizeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 清除数据对话框
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("隐私设置") },
            text = {
                Column {
                    Text("清除浏览数据：")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• 清除缓存")
                    Text("• 清除Cookie")
                    Text("• 清除历史记录")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDataDialog = false
                    }
                ) {
                    Text("清除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SettingItemRow(
    item: SettingItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (item.description != null) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
