package com.chumian.browser

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.chumian.browser.ui.navigation.BottomNavigationBar
import com.chumian.browser.ui.navigation.NavGraph
import com.chumian.browser.ui.theme.ChumianBrowserTheme
import com.chumian.browser.util.ThemeManager
import com.chumian.browser.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 处理权限结果
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 处理启动Intent
        handleIntent(intent)
        
        setContent {
            val navController = rememberNavController()
            mainViewModel = viewModel()
            
            ChumianBrowserTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
        
        // 请求必要权限
        requestNecessaryPermissions()
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val url = intent.dataString
                if (url != null) {
                    // 打开URL
                }
            }
            Intent.ACTION_SEND -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (text != null) {
                    // 处理分享的文本
                }
            }
        }
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
    
    fun enterFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
    
    fun exitFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
