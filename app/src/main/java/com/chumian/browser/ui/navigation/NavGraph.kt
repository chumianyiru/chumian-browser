package com.chumian.browser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chumian.browser.ui.screens.*
import com.chumian.browser.viewmodel.MainViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "browser"
    ) {
        // 浏览器主屏幕
        composable("browser") {
            BrowserScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        
        // 书签
        composable("bookmarks") {
            BookmarksScreen(mainViewModel = mainViewModel)
        }
        
        // 历史记录
        composable("history") {
            HistoryScreen(mainViewModel = mainViewModel)
        }
        
        // 下载
        composable("downloads") {
            DownloadsScreen(mainViewModel = mainViewModel)
        }
        
        // 设置
        composable("settings") {
            SettingsScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        
        // 开发者工具
        composable("devtools") {
            DevToolsScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        
        // 查看源码
        composable("view_source") {
            ViewSourceScreen(mainViewModel = mainViewModel)
        }
        
        // 安全信息
        composable("security_info") {
            SecurityInfoScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        
        // 证书详情
        composable("certificate_details") {
            CertificateDetailsScreen(mainViewModel = mainViewModel)
        }
        
        // 模块管理
        composable("module_manager") {
            ModuleManagerScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        
        // 密码管理
        composable("password_manager") {
            PlaceholderScreen(title = "密码管理")
        }
        
        // 密码生成器
        composable("password_generator") {
            PlaceholderScreen(title = "密码生成器")
        }
        
        // 主题设置
        composable("theme_settings") {
            PlaceholderScreen(title = "主题设置")
        }
        
        // 搜索引擎设置
        composable("search_engine_settings") {
            PlaceholderScreen(title = "搜索引擎设置")
        }
        
        // 隐私设置
        composable("privacy_settings") {
            PlaceholderScreen(title = "隐私设置")
        }
        
        // 安全设置
        composable("security_settings") {
            PlaceholderScreen(title = "安全设置")
        }
        
        // 下载设置
        composable("download_settings") {
            PlaceholderScreen(title = "下载设置")
        }
        
        // 高级设置
        composable("advanced_settings") {
            PlaceholderScreen(title = "高级设置")
        }
        
        // 关于
        composable("about") {
            PlaceholderScreen(title = "关于初眠浏览器")
        }
        
        // 性能分析
        composable("performance") {
            PlaceholderScreen(title = "性能分析")
        }
        
        // 控制台
        composable("console") {
            PlaceholderScreen(title = "控制台")
        }
        
        // 元素审查
        composable("elements") {
            PlaceholderScreen(title = "元素审查")
        }
        
        // 网络监控
        composable("network") {
            PlaceholderScreen(title = "网络监控")
        }
        
        // 资源面板
        composable("resources") {
            PlaceholderScreen(title = "资源面板")
        }
        
        // 源代码面板
        composable("sources") {
            PlaceholderScreen(title = "源代码面板")
        }
        
        // 应用面板
        composable("application") {
            PlaceholderScreen(title = "应用面板")
        }
        
        // 安全面板
        composable("security_panel") {
            PlaceholderScreen(title = "安全面板")
        }
        
        // Lighthouse
        composable("lighthouse") {
            PlaceholderScreen(title = "Lighthouse")
        }
        
        // 内存分析
        composable("memory") {
            PlaceholderScreen(title = "内存分析")
        }
        
        // 审计
        composable("audit") {
            PlaceholderScreen(title = "审计")
        }
        
        // 广告拦截设置
        composable("ad_block_settings") {
            PlaceholderScreen(title = "广告拦截设置")
        }
        
        // 站点设置
        composable("site_settings") {
            PlaceholderScreen(title = "站点设置")
        }
        
        // Cookie管理
        composable("cookie_manager") {
            PlaceholderScreen(title = "Cookie管理")
        }
        
        // 权限设置
        composable("permissions") {
            PlaceholderScreen(title = "权限设置")
        }
        
        // 数据使用
        composable("data_usage") {
            PlaceholderScreen(title = "数据使用")
        }
        
        // 备份与恢复
        composable("backup_restore") {
            PlaceholderScreen(title = "备份与恢复")
        }
        
        // 辅助功能设置
        composable("accessibility_settings") {
            PlaceholderScreen(title = "辅助功能设置")
        }
        
        // 实验室功能
        composable("lab_features") {
            PlaceholderScreen(title = "实验室功能")
        }
        
        // 手势设置
        composable("gesture_settings") {
            PlaceholderScreen(title = "手势设置")
        }
        
        // 外观设置
        composable("appearance_settings") {
            PlaceholderScreen(title = "外观设置")
        }
        
        // 通用设置
        composable("general_settings") {
            PlaceholderScreen(title = "通用设置")
        }
        
        // 标签页管理
        composable("tabs") {
            PlaceholderScreen(title = "标签页管理")
        }
        
        // 全屏模式
        composable("fullscreen") {
            PlaceholderScreen(title = "全屏模式")
        }
        
        // 截图
        composable("screenshot") {
            PlaceholderScreen(title = "截图")
        }
        
        // 分享
        composable("share") {
            PlaceholderScreen(title = "分享")
        }
        
        // 页面信息
        composable("page_info") {
            PlaceholderScreen(title = "页面信息")
        }
        
        // 历史详情
        composable("history_details/{id}") {
            PlaceholderScreen(title = "历史详情")
        }
        
        // 书签详情
        composable("bookmark_details/{id}") {
            PlaceholderScreen(title = "书签详情")
        }
        
        // 下载详情
        composable("download_details/{id}") {
            PlaceholderScreen(title = "下载详情")
        }
        
        // 模块详情
        composable("module_details/{id}") {
            PlaceholderScreen(title = "模块详情")
        }
        
        // 模块商店
        composable("module_store") {
            PlaceholderScreen(title = "模块商店")
        }
        
        // 模块分类
        composable("module_category/{category}") {
            PlaceholderScreen(title = "模块分类")
        }
        
        // 模块搜索
        composable("module_search") {
            PlaceholderScreen(title = "模块搜索")
        }
        
        // 我的模块
        composable("my_modules") {
            PlaceholderScreen(title = "我的模块")
        }
        
        // 创建模块
        composable("create_module") {
            PlaceholderScreen(title = "创建模块")
        }
        
        // 导入模块
        composable("import_module") {
            PlaceholderScreen(title = "导入模块")
        }
        
        // 导出模块
        composable("export_module") {
            PlaceholderScreen(title = "导出模块")
        }
        
        // 模块API文档
        composable("module_api_docs") {
            PlaceholderScreen(title = "模块API文档")
        }
        
        // 模块教程
        composable("module_tutorial") {
            PlaceholderScreen(title = "模块教程")
        }
        
        // 模块常见问题
        composable("module_faq") {
            PlaceholderScreen(title = "常见问题")
        }
        
        // 模块支持
        composable("module_support") {
            PlaceholderScreen(title = "技术支持")
        }
        
        // 模块反馈
        composable("module_feedback") {
            PlaceholderScreen(title = "意见反馈")
        }
        
        // 阅读模式
        composable("reading_mode") {
            PlaceholderScreen(title = "阅读模式")
        }
        
        // 翻译
        composable("translate") {
            PlaceholderScreen(title = "网页翻译")
        }
        
        // 页面内查找
        composable("find_in_page") {
            PlaceholderScreen(title = "页面内查找")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Build,
                contentDescription = null,
                modifier = androidx.compose.ui.Modifier.size(64.dp),
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = "功能开发中...",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
