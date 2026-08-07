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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chumian.browser.data.model.DownloadItem
import com.chumian.browser.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DownloadsScreen(mainViewModel: MainViewModel) {
    val downloads by mainViewModel.downloads.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("全部", "下载中", "已完成", "失败")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("下载管理") },
                actions = {
                    IconButton(onClick = { /* 搜索 */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = { /* 设置 */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
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
            // 标签页
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // 下载列表
            val filteredDownloads = when (selectedTab) {
                1 -> downloads.filter { it.status == DownloadItem.STATUS_DOWNLOADING }
                2 -> downloads.filter { it.status == DownloadItem.STATUS_COMPLETED }
                3 -> downloads.filter { it.status == DownloadItem.STATUS_FAILED }
                else -> downloads
            }
            
            if (filteredDownloads.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无下载记录",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "下载的文件会显示在这里",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredDownloads) { download ->
                        DownloadItemCard(
                            download = download,
                            onClick = { /* 打开文件 */ },
                            onPause = { /* 暂停 */ },
                            onResume = { /* 继续 */ },
                            onCancel = { /* 取消 */ },
                            onDelete = { /* 删除 */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    download: DownloadItem,
    onClick: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getFileIcon(download.mimeType),
                    contentDescription = null,
                    tint = getFileIconColor(download.mimeType),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = download.fileName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatFileSize(download.totalSize),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "更多")
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (download.status == DownloadItem.STATUS_COMPLETED) {
                            DropdownMenuItem(
                                text = { Text("打开") },
                                onClick = {
                                    onClick()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("打开所在文件夹") },
                                onClick = { showMenu = false },
                                leadingIcon = {
                                    Icon(Icons.Default.Folder, contentDescription = null)
                                }
                            )
                        }
                        if (download.status == DownloadItem.STATUS_DOWNLOADING) {
                            DropdownMenuItem(
                                text = { Text("暂停") },
                                onClick = {
                                    onPause()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Pause, contentDescription = null)
                                }
                            )
                        }
                        if (download.status == DownloadItem.STATUS_PAUSED) {
                            DropdownMenuItem(
                                text = { Text("继续") },
                                onClick = {
                                    onResume()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                }
                            )
                        }
                        if (download.status == DownloadItem.STATUS_DOWNLOADING || 
                            download.status == DownloadItem.STATUS_PAUSED) {
                            DropdownMenuItem(
                                text = { Text("取消") },
                                onClick = {
                                    onCancel()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Cancel, contentDescription = null)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
            
            // 进度条
            if (download.status == DownloadItem.STATUS_DOWNLOADING || 
                download.status == DownloadItem.STATUS_PAUSED) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 
                        if (download.totalSize > 0) {
                            download.downloadedSize.toFloat() / download.totalSize.toFloat()
                        } else {
                            0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${formatFileSize(download.downloadedSize)} / ${formatFileSize(download.totalSize)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatFileSize(download.speed)}/s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 状态
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getStatusText(download.status),
                style = MaterialTheme.typography.labelSmall,
                color = getStatusColor(download.status)
            )
        }
    }
}

fun getFileIcon(mimeType: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        mimeType.startsWith("image/") -> Icons.Default.Image
        mimeType.startsWith("video/") -> Icons.Default.VideoLibrary
        mimeType.startsWith("audio/") -> Icons.Default.AudioFile
        mimeType.startsWith("application/pdf") -> Icons.Default.PictureAsPdf
        mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("tar") -> Icons.Default.FolderZip
        mimeType.contains("apk") -> Icons.Default.Android
        else -> Icons.Default.InsertDriveFile
    }
}

fun getFileIconColor(mimeType: String): androidx.compose.ui.graphics.Color {
    return when {
        mimeType.startsWith("image/") -> androidx.compose.ui.graphics.Color(0xFFE91E63)
        mimeType.startsWith("video/") -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
        mimeType.startsWith("audio/") -> androidx.compose.ui.graphics.Color(0xFF673AB7)
        mimeType.startsWith("application/pdf") -> androidx.compose.ui.graphics.Color(0xFFF44336)
        else -> androidx.compose.ui.graphics.Color(0xFF2196F3)
    }
}

fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}

fun getStatusText(status: Int): String {
    return when (status) {
        DownloadItem.STATUS_PENDING -> "等待中"
        DownloadItem.STATUS_DOWNLOADING -> "下载中"
        DownloadItem.STATUS_PAUSED -> "已暂停"
        DownloadItem.STATUS_COMPLETED -> "已完成"
        DownloadItem.STATUS_FAILED -> "失败"
        DownloadItem.STATUS_CANCELLED -> "已取消"
        else -> "未知"
    }
}

fun getStatusColor(status: Int): androidx.compose.ui.graphics.Color {
    return when (status) {
        DownloadItem.STATUS_DOWNLOADING -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        DownloadItem.STATUS_COMPLETED -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        DownloadItem.STATUS_FAILED -> androidx.compose.ui.graphics.Color(0xFFF44336)
        DownloadItem.STATUS_PAUSED -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        else -> androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    }
}
