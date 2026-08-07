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
fun SecurityInfoScreen(
    mainViewModel: MainViewModel,
    navController: NavController? = null
) {
    val currentUrl by mainViewModel.currentUrl.collectAsState()
    val securityInfo by mainViewModel.securityInfo.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(currentUrl) {
        isLoading = true
        mainViewModel.loadSecurityInfo(currentUrl)
        isLoading = false
    }
    
    val rating = securityInfo["rating"] ?: "N/A"
    val isHttps = securityInfo["is_https"]?.toBoolean() ?: false
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("安全信息") }
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 安全评级卡片
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = getRatingColor(rating)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (isHttps) {
                                    Icons.Default.Verified
                                } else {
                                    Icons.Default.Warning
                                },
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "安全评级: $rating",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHttps) {
                                    "此网站使用HTTPS加密连接"
                                } else {
                                    "此网站未使用HTTPS加密，请注意安全"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                
                // 网站信息
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
                                text = "网站信息",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            InfoRow("URL", currentUrl)
                            InfoRow("协议", securityInfo["protocol"] ?: "未知")
                            InfoRow("加密套件", securityInfo["cipher_suite"] ?: "未知")
                        }
                    }
                }
                
                // 证书信息
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController?.navigate("certificate_details") }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Certificate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "证书详情",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "查看SSL证书详细信息",
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
                
                // 安全提示
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
                                text = "安全提示",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "• 始终检查网站是否使用HTTPS加密连接\n" +
                                       "• 不要在不信任的网站上输入个人信息\n" +
                                       "• 定期清除浏览器缓存和Cookie\n" +
                                       "• 使用强密码并定期更换\n" +
                                       "• 启用双因素认证",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

fun getRatingColor(rating: String): androidx.compose.ui.graphics.Color {
    return when (rating) {
        "A+", "A" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        "B" -> androidx.compose.ui.graphics.Color(0xFF8BC34A)
        "C" -> androidx.compose.ui.graphics.Color(0xFFFFC107)
        "D" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        "F" -> androidx.compose.ui.graphics.Color(0xFFF44336)
        else -> androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    }
}
