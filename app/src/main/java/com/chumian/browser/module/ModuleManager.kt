package com.chumian.browser.module

import android.content.Context
import com.chumian.browser.ChumianApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

data class Module(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val author: String,
    val icon: String? = null,
    val category: String = "other",
    val isEnabled: Boolean = true,
    val isInstalled: Boolean = true,
    val installTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis(),
    val permissions: List<String> = emptyList(),
    val entryPoint: String = "",
    val modulePath: String = ""
)

class ModuleManager(private val context: Context) {
    private val modulesDir: File = File(context.filesDir, "modules")
    private val installedModules = mutableMapOf<String, Module>()
    
    init {
        if (!modulesDir.exists()) {
            modulesDir.mkdirs()
        }
    }
    
    suspend fun initializeBuiltInModules() {
        withContext(Dispatchers.IO) {
            // 初始化内置模块：初始页百度
            val baiduModule = Module(
                id = "builtin_baidu_homepage",
                name = "初始页百度",
                version = "1.0.0",
                description = "内置的百度搜索初始页模块，提供快速搜索功能",
                author = "初眠浏览器",
                category = "search",
                isEnabled = true,
                isInstalled = true,
                permissions = listOf("search", "homepage"),
                entryPoint = "builtin://baidu-homepage"
            )
            installedModules[baiduModule.id] = baiduModule
            
            // 加载已安装的模块
            loadInstalledModules()
        }
    }
    
    private fun loadInstalledModules() {
        if (!modulesDir.exists()) return
        
        modulesDir.listFiles()?.forEach { moduleDir ->
            if (moduleDir.isDirectory) {
                val manifestFile = File(moduleDir, "manifest.json")
                if (manifestFile.exists()) {
                    try {
                        val manifest = JSONObject(manifestFile.readText())
                        val module = Module(
                            id = manifest.getString("id"),
                            name = manifest.getString("name"),
                            version = manifest.optString("version", "1.0.0"),
                            description = manifest.optString("description", ""),
                            author = manifest.optString("author", ""),
                            icon = manifest.optString("icon", null),
                            category = manifest.optString("category", "other"),
                            isEnabled = manifest.optBoolean("enabled", true),
                            isInstalled = true,
                            permissions = manifest.optJSONArray("permissions")?.let { jsonArray ->
                                List(jsonArray.length()) { i -> jsonArray.getString(i) }
                            } ?: emptyList(),
                            entryPoint = manifest.optString("entry_point", ""),
                            modulePath = moduleDir.absolutePath
                        )
                        installedModules[module.id] = module
                    } catch (e: Exception) {
                        // 忽略无效的模块
                    }
                }
            }
        }
    }
    
    fun getInstalledModules(): List<Module> {
        return installedModules.values.toList()
    }
    
    fun getModuleById(id: String): Module? {
        return installedModules[id]
    }
    
    fun getModulesByCategory(category: String): List<Module> {
        return installedModules.values.filter { it.category == category }
    }
    
    fun getEnabledModules(): List<Module> {
        return installedModules.values.filter { it.isEnabled }
    }
    
    suspend fun installModule(modulePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sourceFile = File(modulePath)
                if (!sourceFile.exists()) return@withContext false
                
                // 这里简化处理，实际应该解压模块包
                // 假设modulePath是一个目录，包含manifest.json
                val manifestFile = File(sourceFile, "manifest.json")
                if (!manifestFile.exists()) return@withContext false
                
                val manifest = JSONObject(manifestFile.readText())
                val moduleId = manifest.getString("id")
                
                // 复制到模块目录
                val targetDir = File(modulesDir, moduleId)
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }
                
                sourceFile.copyRecursively(targetDir, overwrite = true)
                
                // 加载模块
                val module = Module(
                    id = moduleId,
                    name = manifest.getString("name"),
                    version = manifest.optString("version", "1.0.0"),
                    description = manifest.optString("description", ""),
                    author = manifest.optString("author", ""),
                    icon = manifest.optString("icon", null),
                    category = manifest.optString("category", "other"),
                    isEnabled = true,
                    isInstalled = true,
                    installTime = System.currentTimeMillis(),
                    permissions = manifest.optJSONArray("permissions")?.let { jsonArray ->
                        List(jsonArray.length()) { i -> jsonArray.getString(i) }
                    } ?: emptyList(),
                    entryPoint = manifest.optString("entry_point", ""),
                    modulePath = targetDir.absolutePath
                )
                
                installedModules[moduleId] = module
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    suspend fun uninstallModule(moduleId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val module = installedModules[moduleId] ?: return@withContext false
                
                // 删除模块目录
                val moduleDir = File(modulesDir, moduleId)
                if (moduleDir.exists()) {
                    moduleDir.deleteRecursively()
                }
                
                installedModules.remove(moduleId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    fun enableModule(moduleId: String): Boolean {
        val module = installedModules[moduleId] ?: return false
        installedModules[moduleId] = module.copy(isEnabled = true)
        return true
    }
    
    fun disableModule(moduleId: String): Boolean {
        val module = installedModules[moduleId] ?: return false
        installedModules[moduleId] = module.copy(isEnabled = false)
        return true
    }
    
    fun isModuleEnabled(moduleId: String): Boolean {
        return installedModules[moduleId]?.isEnabled ?: false
    }
    
    fun isModuleInstalled(moduleId: String): Boolean {
        return installedModules.containsKey(moduleId)
    }
    
    fun getModuleCategories(): List<String> {
        return listOf(
            "search",
            "tools",
            "security",
            "productivity",
            "entertainment",
            "social",
            "developer",
            "other"
        )
    }
    
    fun getCategoryName(category: String): String {
        return when (category) {
            "search" -> "搜索"
            "tools" -> "工具"
            "security" -> "安全"
            "productivity" -> "效率"
            "entertainment" -> "娱乐"
            "social" -> "社交"
            "developer" -> "开发者"
            "other" -> "其他"
            else -> category
        }
    }
    
    fun searchModules(query: String): List<Module> {
        val lowerQuery = query.lowercase()
        return installedModules.values.filter { module ->
            module.name.lowercase().contains(lowerQuery) ||
            module.description.lowercase().contains(lowerQuery) ||
            module.author.lowercase().contains(lowerQuery)
        }
    }
    
    fun getBuiltInModules(): List<Module> {
        return installedModules.values.filter { it.id.startsWith("builtin_") }
    }
    
    fun getThirdPartyModules(): List<Module> {
        return installedModules.values.filter { !it.id.startsWith("builtin_") }
    }
    
    fun getModuleCount(): Int {
        return installedModules.size
    }
    
    fun getEnabledModuleCount(): Int {
        return installedModules.values.count { it.isEnabled }
    }
    
    // 模块API - 搜索模块
    fun getSearchModules(): List<Module> {
        return getEnabledModules().filter { it.permissions.contains("search") }
    }
    
    // 模块API - 主页模块
    fun getHomepageModules(): List<Module> {
        return getEnabledModules().filter { it.permissions.contains("homepage") }
    }
    
    // 模块API - 工具模块
    fun getToolModules(): List<Module> {
        return getEnabledModules().filter { it.permissions.contains("tool") }
    }
    
    // 执行模块功能
    suspend fun executeModule(moduleId: String, action: String, params: Map<String, String> = emptyMap()): String? {
        return withContext(Dispatchers.IO) {
            val module = installedModules[moduleId] ?: return@withContext null
            
            // 这里简化处理，实际应该执行模块的代码
            // 对于内置模块，直接返回结果
            if (moduleId == "builtin_baidu_homepage") {
                when (action) {
                    "search" -> {
                        val query = params["query"] ?: ""
                        "https://www.baidu.com/s?wd=$query"
                    }
                    "homepage" -> {
                        "https://www.baidu.com"
                    }
                    else -> null
                }
            } else {
                // 第三方模块需要执行对应的脚本
                null
            }
        }
    }
}
