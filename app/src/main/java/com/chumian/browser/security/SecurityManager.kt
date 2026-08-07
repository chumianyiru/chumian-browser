package com.chumian.browser.security

import android.content.Context
import android.net.http.SslCertificate
import com.chumian.browser.ChumianApp
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLPeerUnverifiedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecurityManager(private val context: Context) {
    private val okHttpClient = OkHttpClient()
    
    suspend fun checkWebsiteSecurity(url: String): Map<String, String> {
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, String>()
            
            try {
                val uri = android.net.Uri.parse(url)
                val host = uri.host ?: return@withContext mapOf("error" to "无效的URL")
                
                // 检查是否为HTTPS
                val isHttps = url.startsWith("https://")
                result["is_https"] = isHttps.toString()
                
                if (isHttps) {
                    // 获取证书信息
                    val certInfo = getCertificateInfo(url)
                    result.putAll(certInfo)
                    
                    // 计算安全评级
                    val rating = calculateSecurityRating(result)
                    result["rating"] = rating
                } else {
                    result["rating"] = "F"
                    result["warning"] = "非HTTPS连接，数据传输不安全"
                }
                
                result["url"] = url
                result["host"] = host
                
            } catch (e: Exception) {
                result["error"] = e.message ?: "未知错误"
                result["rating"] = "F"
            }
            
            result
        }
    }
    
    suspend fun getCertificateInfo(url: String): Map<String, String> {
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, String>()
            
            try {
                val uri = java.net.URI(url)
                val urlObj = uri.toURL()
                val connection = urlObj.openConnection() as HttpsURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                try {
                    connection.connect()
                    
                    val certificates = connection.serverCertificates
                    if (certificates.isNotEmpty()) {
                        val cert = certificates[0] as X509Certificate
                        
                        result["subject"] = cert.subjectDN.name
                        result["issuer"] = cert.issuerDN.name
                        result["valid_from"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(cert.notBefore)
                        result["valid_to"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(cert.notAfter)
                        result["algorithm"] = cert.sigAlgName
                        result["version"] = cert.version.toString()
                        result["serial_number"] = cert.serialNumber.toString(16)
                        result["public_key_algorithm"] = cert.publicKey.algorithm
                        result["public_key_format"] = cert.publicKey.format
                        
                        // 检查证书是否过期
                        val now = Date()
                        val isValid = now.after(cert.notBefore) && now.before(cert.notAfter)
                        result["is_valid"] = isValid.toString()
                        
                        // 计算剩余天数
                        val remainingDays = (cert.notAfter.time - now.time) / (1000 * 60 * 60 * 24)
                        result["remaining_days"] = remainingDays.toString()
                        
                        // 提取主题备用名称
                        try {
                            val san = cert.subjectAlternativeNames
                            if (san != null) {
                                val sanList = san.joinToString(", ") { it[1].toString() }
                                result["subject_alt_names"] = sanList
                            }
                        } catch (e: Exception) {
                            // 忽略
                        }
                    }
                    
                    result["cipher_suite"] = connection.cipherSuite
                    result["protocol"] = connection.protocol
                    
                } finally {
                    connection.disconnect()
                }
                
            } catch (e: SSLPeerUnverifiedException) {
                result["error"] = "SSL证书验证失败: ${e.message}"
            } catch (e: Exception) {
                result["error"] = "获取证书信息失败: ${e.message}"
            }
            
            result
        }
    }
    
    private fun calculateSecurityRating(info: Map<String, String>): String {
        var score = 100
        
        // 检查是否为HTTPS
        if (info["is_https"] != "true") {
            return "F"
        }
        
        // 检查证书是否有效
        if (info["is_valid"] != "true") {
            score -= 50
        }
        
        // 检查剩余天数
        val remainingDays = info["remaining_days"]?.toIntOrNull() ?: 0
        if (remainingDays < 30) {
            score -= 20
        } else if (remainingDays < 90) {
            score -= 10
        }
        
        // 检查加密算法
        val algorithm = info["algorithm"]?.lowercase() ?: ""
        if (algorithm.contains("md5") || algorithm.contains("sha1")) {
            score -= 30
        }
        
        // 检查公钥算法
        val publicKeyAlgorithm = info["public_key_algorithm"]?.lowercase() ?: ""
        if (publicKeyAlgorithm.contains("rsa")) {
            // RSA 2048位是标准
        } else if (publicKeyAlgorithm.contains("ec")) {
            // ECC更安全
            score += 5
        }
        
        // 检查协议版本
        val protocol = info["protocol"]?.lowercase() ?: ""
        if (protocol.contains("tlsv1.3")) {
            score += 10
        } else if (protocol.contains("tlsv1.2")) {
            // 标准
        } else if (protocol.contains("tlsv1.1") || protocol.contains("tlsv1")) {
            score -= 25
        } else if (protocol.contains("sslv3")) {
            score -= 50
        }
        
        return when {
            score >= 90 -> "A+"
            score >= 80 -> "A"
            score >= 70 -> "B"
            score >= 60 -> "C"
            score >= 50 -> "D"
            else -> "F"
        }
    }
    
    fun generateStrongPassword(
        length: Int = 16,
        includeUppercase: Boolean = true,
        includeLowercase: Boolean = true,
        includeNumbers: Boolean = true,
        includeSymbols: Boolean = true
    ): String {
        val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
        val numberChars = "0123456789"
        val symbolChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"
        
        var chars = ""
        if (includeUppercase) chars += uppercaseChars
        if (includeLowercase) chars += lowercaseChars
        if (includeNumbers) chars += numberChars
        if (includeSymbols) chars += symbolChars
        
        if (chars.isEmpty()) {
            chars = lowercaseChars
        }
        
        val password = StringBuilder()
        val random = java.security.SecureRandom()
        
        // 确保至少包含每种类型的一个字符
        if (includeUppercase) {
            password.append(uppercaseChars[random.nextInt(uppercaseChars.length)])
        }
        if (includeLowercase) {
            password.append(lowercaseChars[random.nextInt(lowercaseChars.length)])
        }
        if (includeNumbers) {
            password.append(numberChars[random.nextInt(numberChars.length)])
        }
        if (includeSymbols) {
            password.append(symbolChars[random.nextInt(symbolChars.length)])
        }
        
        // 填充剩余长度
        while (password.length < length) {
            password.append(chars[random.nextInt(chars.length)])
        }
        
        // 打乱顺序
        return password.toString().toCharArray().apply {
            for (i in indices) {
                val j = random.nextInt(size)
                val temp = this[i]
                this[i] = this[j]
                this[j] = temp
            }
        }.concatToString()
    }
    
    fun checkPasswordStrength(password: String): Pair<String, Int> {
        var score = 0
        
        // 长度
        when {
            password.length >= 16 -> score += 25
            password.length >= 12 -> score += 20
            password.length >= 8 -> score += 10
            else -> score += 5
        }
        
        // 包含大写字母
        if (password.any { it.isUpperCase() }) score += 20
        
        // 包含小写字母
        if (password.any { it.isLowerCase() }) score += 15
        
        // 包含数字
        if (password.any { it.isDigit() }) score += 20
        
        // 包含特殊字符
        if (password.any { !it.isLetterOrDigit() }) score += 20
        
        val strength = when {
            score >= 90 -> "非常强"
            score >= 70 -> "强"
            score >= 50 -> "中等"
            score >= 30 -> "弱"
            else -> "非常弱"
        }
        
        return strength to score
    }
    
    fun isPhishingUrl(url: String): Boolean {
        val lowerUrl = url.lowercase()
        
        // 检查常见的钓鱼特征
        val suspiciousPatterns = listOf(
            "login", "signin", "verify", "account", "update",
            "confirm", "password", "security", "bank", "paypal",
            "amazon", "google", "facebook", "apple", "microsoft"
        )
        
        // 检查是否有多个子域名（钓鱼网站常用）
        val dotCount = lowerUrl.count { it == '.' }
        if (dotCount > 3) return true
        
        // 检查是否包含IP地址而非域名
        val ipPattern = Regex("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
        if (ipPattern.containsMatchIn(lowerUrl)) return true
        
        return false
    }
    
    fun getSecurityTips(): List<String> {
        return listOf(
            "始终检查网站是否使用HTTPS加密连接",
            "不要在不信任的网站上输入个人信息",
            "定期清除浏览器缓存和Cookie",
            "使用强密码并定期更换",
            "启用双因素认证",
            "不要点击可疑的链接或下载附件",
            "保持浏览器和操作系统更新",
            "使用广告拦截器防止恶意广告",
            "定期检查已保存的密码",
            "使用无痕模式浏览敏感内容"
        )
    }
}
