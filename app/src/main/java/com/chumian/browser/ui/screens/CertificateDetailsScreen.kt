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
import com.chumian.browser.viewmodel.MainViewModel

@Composable
fun CertificateDetailsScreen(mainViewModel: MainViewModel) {
    val certificateInfo by mainViewModel.certificateInfo.collectAsState()
    val currentUrl by mainViewModel.currentUrl.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(currentUrl) {
        isLoading = true
        mainViewModel.loadCertificateInfo(currentUrl)
        isLoading = false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("证书详情") }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (certificateInfo.containsKey("error")) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "无法获取证书信息",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = certificateInfo["error"] ?: "未知错误",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 证书状态
                item {
                    val isValid = certificateInfo["is_valid"]?.toBoolean() ?: false
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isValid) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isValid) {
                                    Icons.Default.Verified
                                } else {
                                    Icons.Default.Warning
                                },
                                contentDescription = null,
                                tint = if (isValid) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                },
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (isValid) "证书有效" else "证书无效",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (isValid) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onErrorContainer
                                    }
                                )
                                Text(
                                    text = "剩余 ${certificateInfo["remaining_days"] ?: "0"} 天",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isValid) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onErrorContainer
                                    }
                                )
                            }
                        }
                    }
                }
                
                // 主题信息
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "主题信息",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow("主题 (Subject)", certificateInfo["subject"] ?: "未知")
                            InfoRow("颁发者 (Issuer)", certificateInfo["issuer"] ?: "未知")
                        }
                    }
                }
                
                // 有效期
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "有效期",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow("生效时间", certificateInfo["valid_from"] ?: "未知")
                            InfoRow("到期时间", certificateInfo["valid_to"] ?: "未知")
                            InfoRow("剩余天数", "${certificateInfo["remaining_days"] ?: "0"} 天")
                        }
                    }
                }
                
                // 证书详情
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "证书详情",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow("版本 (Version)", certificateInfo["version"] ?: "未知")
                            InfoRow("序列号", certificateInfo["serial_number"] ?: "未知")
                            InfoRow("签名算法", certificateInfo["algorithm"] ?: "未知")
                            InfoRow("公钥算法", certificateInfo["public_key_algorithm"] ?: "未知")
                            InfoRow("公钥格式", certificateInfo["public_key_format"] ?: "未知")
                        }
                    }
                }
                
                // 主题备用名称
                item {
                    val san = certificateInfo["subject_alt_names"]
                    if (san != null && san.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "主题备用名称 (SAN)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = san,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // 连接信息
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "连接信息",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow("协议版本", certificateInfo["protocol"] ?: "未知")
                            InfoRow("加密套件", certificateInfo["cipher_suite"] ?: "未知")
                        }
                    }
                }
            }
        }
    }
}
