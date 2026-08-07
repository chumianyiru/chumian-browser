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

data class SettingItem(
    val title: String,
    val description: String? = null,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val settings = remember {
        listOf(
            SettingItem(
                title = "搜索引擎",
                description = "百度",
                icon = Icons.Default.Search
            ),
            SettingItem(
                title = "主题",
                description = "跟随系统",
                icon = Icons.Default.Palette
            ),
            SettingItem(
                title = "字体大小",
                description = "默认",
                icon = Icons.Default.FormatSize
            ),
            SettingItem(
                title = "隐私设置",
                description = "清除缓存、Cookie等",
                icon = Icons.Default.Security
            ),
            SettingItem(
                title = "关于",
                description = "初眠浏览器 v1.0.0",
                icon = Icons.Default.Info
            )
        )
    }

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
                HorizontalDivider()
            }
        }
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
