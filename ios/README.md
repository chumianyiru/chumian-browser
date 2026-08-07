# 初眠浏览器 iOS 版

基于 SwiftUI + WKWebKit 原生开发的 iOS 浏览器应用。

## 技术栈

- **语言**: Swift 5
- **UI 框架**: SwiftUI
- **Web 引擎**: WKWebView
- **最低支持**: iOS 15.0
- **项目管理**: XcodeGen

## 功能列表

### ✅ 已实现功能

#### 核心浏览
- [x] WKWebView 网页浏览
- [x] 地址栏 + 搜索栏合一
- [x] 前进/后退/刷新/停止
- [x] 加载进度条显示
- [x] 手势滑动导航

#### 标签页管理
- [x] 多标签页管理
- [x] 新建/关闭/切换标签页
- [x] 标签页预览卡片
- [x] 一键关闭全部标签页

#### 书签管理
- [x] 添加/删除书签
- [x] 书签文件夹分类
- [x] 书签搜索
- [x] 书签编辑
- [x] 本地持久化存储

#### 历史记录
- [x] 自动记录浏览历史
- [x] 按日期分组显示
- [x] 历史记录搜索
- [x] 单条删除/批量清除
- [x] 按时间范围清除

#### 下载管理
- [x] 下载进度跟踪
- [x] 下载状态管理（下载中/已完成/失败/暂停）
- [x] 下载列表展示
- [x] 文件分类图标
- [x] 本地文件管理

#### 设置页面
- [x] 搜索引擎切换（百度/谷歌/必应/搜狗/360）
- [x] 主题切换（浅色/深色/跟随系统）
- [x] 字体大小调节（50%-200%）
- [x] 桌面模式/移动模式切换
- [x] 首页设置
- [x] 阻止弹窗开关
- [x] 保存历史记录开关

#### 高级功能
- [x] 查看网页源码
- [x] 页面内查找（上一个/下一个）
- [x] 无痕浏览模式
- [x] 桌面模式/移动模式切换（User-Agent）
- [x] 分享功能
- [x] 清除缓存/Cookie
- [x] 安全信息查看（HTTPS 状态）

#### UI 设计
- [x] 全中文界面
- [x] 精致美观的 UI 设计
- [x] 适配深色/浅色模式
- [x] 适配 iPhone 和 iPad
- [x] 支持横竖屏切换

## 项目结构

```
ios/
├── ChumianBrowser/
│   ├── ChumianBrowserApp.swift    # 应用入口
│   ├── Info.plist                 # 应用配置
│   ├── Assets.xcassets/           # 资源文件
│   ├── Models/                    # 数据模型
│   │   ├── Bookmark.swift
│   │   ├── HistoryItem.swift
│   │   └── DownloadItem.swift
│   ├── Managers/                  # 业务管理器
│   │   ├── BookmarkManager.swift
│   │   ├── HistoryManager.swift
│   │   ├── DownloadManager.swift
│   │   ├── SettingsManager.swift
│   │   └── TabManager.swift
│   └── Views/                     # 视图层
│       ├── ContentView.swift
│       ├── BrowserView.swift      # 主浏览器界面
│       ├── WebView.swift          # WKWebView 封装
│       ├── TabsView.swift         # 标签页管理
│       ├── BookmarksView.swift    # 书签管理
│       ├── HistoryView.swift      # 历史记录
│       ├── DownloadsView.swift    # 下载管理
│       ├── SettingsView.swift     # 设置页面
│       └── SourceCodeView.swift   # 源码查看
├── project.yml                    # XcodeGen 项目配置
└── ChumianBrowser.xcodeproj/      # Xcode 项目文件
```

## 编译说明

### 使用 XcodeGen 生成项目

```bash
brew install xcodegen
cd ios
xcodegen generate
open ChumianBrowser.xcodeproj
```

### 使用 Xcode 编译

1. 打开 `ChumianBrowser.xcodeproj`
2. 选择模拟器或真机
3. 点击运行按钮

### GitHub Actions 自动编译

项目已配置 GitHub Actions 工作流，每次推送代码到 main 分支会自动触发编译：

- 模拟器版本（iPhone 15）
- 真机版本（Generic iOS Device）
- 编译产物自动上传为 Artifact

## 关于 iOS 12 支持的说明

由于 SwiftUI 框架要求最低 iOS 13，且 WKWebView 的部分现代 API 需要更高版本，本项目最低支持 iOS 15.0。

如果需要支持 iOS 12，需要改用 UIKit 框架重写 UI 层，并降低 WKWebView API 的使用版本。

## 开源协议

MIT License
