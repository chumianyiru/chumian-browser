import SwiftUI

struct DownloadsView: View {
    @EnvironmentObject var downloadManager: DownloadManager
    @Environment(\.presentationMode) var presentationMode
    
    @State private var showClearDialog = false
    @State private var selectedSegment = 0
    
    var displayedDownloads: [DownloadItem] {
        switch selectedSegment {
        case 0:
            return downloadManager.downloads
        case 1:
            return downloadManager.activeDownloads
        case 2:
            return downloadManager.completedDownloads
        default:
            return downloadManager.downloads
        }
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 分段选择
                Picker("下载分类", selection: $selectedSegment) {
                    Text("全部").tag(0)
                    Text("进行中").tag(1)
                    Text("已完成").tag(2)
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding(.horizontal)
                .padding(.vertical, 8)
                
                if displayedDownloads.isEmpty {
                    VStack(spacing: 16) {
                        Spacer()
                        Image(systemName: "arrow.down.circle")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("暂无下载记录")
                            .foregroundColor(.gray)
                        Text("下载的文件会显示在这里")
                            .font(.caption)
                            .foregroundColor(.gray.opacity(0.7))
                        Spacer()
                    }
                } else {
                    List {
                        ForEach(displayedDownloads) { item in
                            DownloadItemRow(item: item)
                                .contextMenu {
                                    if item.status == .downloading {
                                        Button(action: {
                                            downloadManager.pauseDownload(id: item.id)
                                        }) {
                                            Label("暂停", systemImage: "pause")
                                        }
                                    }
                                    
                                    if item.status == .paused {
                                        Button(action: {
                                            downloadManager.resumeDownload(id: item.id)
                                        }) {
                                            Label("继续", systemImage: "play")
                                        }
                                    }
                                    
                                    if item.status == .completed {
                                        Button(action: {
                                            // 打开文件
                                        }) {
                                            Label("打开文件", systemImage: "doc")
                                        }
                                        
                                        Button(action: {
                                            // 分享文件
                                        }) {
                                            Label("分享", systemImage: "square.and.arrow.up")
                                        }
                                    }
                                    
                                    Button(action: {
                                        downloadManager.removeDownload(item)
                                    }) {
                                        Label("删除", systemImage: "trash")
                                    }
                                }
                        }
                        .onDelete { offsets in
                            let itemsToDelete = offsets.map { displayedDownloads[$0] }
                            itemsToDelete.forEach { downloadManager.removeDownload($0) }
                        }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            .navigationTitle("下载管理")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    if !displayedDownloads.isEmpty {
                        Button(action: { showClearDialog = true }) {
                            Image(systemName: "trash")
                                .foregroundColor(.red)
                        }
                    }
                }
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .actionSheet(isPresented: $showClearDialog) {
                ActionSheet(
                    title: Text("清除下载记录"),
                    buttons: [
                        .destructive(Text("清除已完成的下载")) {
                            downloadManager.clearCompleted()
                        },
                        .destructive(Text("清除全部下载记录")) {
                            downloadManager.clearAll()
                        },
                        .cancel()
                    ]
                )
            }
        }
    }
}

struct DownloadItemRow: View {
    let item: DownloadItem
    @EnvironmentObject var downloadManager: DownloadManager
    
    var body: some View {
        HStack(spacing: 12) {
            // 文件图标
            ZStack {
                RoundedRectangle(cornerRadius: 8)
                    .fill(fileIconColor.opacity(0.1))
                Image(systemName: fileIconName)
                    .font(.system(size: 18))
                    .foregroundColor(fileIconColor)
            }
            .frame(width: 40, height: 40)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(item.fileName)
                    .font(.system(size: 14))
                    .lineLimit(1)
                
                HStack(spacing: 8) {
                    Text(statusText)
                        .font(.system(size: 12))
                        .foregroundColor(statusColor)
                    
                    if item.status == .downloading {
                        Text(downloadManager.formattedFileSize(item.downloadedSize))
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                        
                        Text("/")
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                        
                        Text(downloadManager.formattedFileSize(item.fileSize))
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                    } else if item.status == .completed {
                        Text(downloadManager.formattedFileSize(item.fileSize))
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                    }
                }
                
                if item.status == .downloading {
                    ProgressView(value: item.progress)
                        .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                }
            }
            
            Spacer()
            
            // 状态图标
            Image(systemName: statusIcon)
                .foregroundColor(statusColor)
                .font(.system(size: 16))
        }
        .padding(.vertical, 6)
    }
    
    private var fileIconName: String {
        let ext = (item.fileName as NSString).pathExtension.lowercased()
        switch ext {
        case "pdf":
            return "doc.fill"
        case "zip", "rar", "7z":
            return "doc.zipper"
        case "png", "jpg", "jpeg", "gif", "webp":
            return "photo.fill"
        case "mp4", "mov", "avi", "mkv":
            return "film.fill"
        case "mp3", "wav", "flac", "aac":
            return "music.note"
        case "html", "htm":
            return "globe"
        default:
            return "doc.fill"
        }
    }
    
    private var fileIconColor: Color {
        let ext = (item.fileName as NSString).pathExtension.lowercased()
        switch ext {
        case "pdf":
            return .red
        case "zip", "rar", "7z":
            return .orange
        case "png", "jpg", "jpeg", "gif", "webp":
            return .green
        case "mp4", "mov", "avi", "mkv":
            return .purple
        case "mp3", "wav", "flac", "aac":
            return .pink
        default:
            return .blue
        }
    }
    
    private var statusText: String {
        switch item.status {
        case .downloading:
            return "下载中"
        case .completed:
            return "已完成"
        case .failed:
            return "下载失败"
        case .paused:
            return "已暂停"
        }
    }
    
    private var statusColor: Color {
        switch item.status {
        case .downloading:
            return .blue
        case .completed:
            return .green
        case .failed:
            return .red
        case .paused:
            return .orange
        }
    }
    
    private var statusIcon: String {
        switch item.status {
        case .downloading:
            return "arrow.down.circle"
        case .completed:
            return "checkmark.circle.fill"
        case .failed:
            return "exclamationmark.circle.fill"
        case .paused:
            return "pause.circle.fill"
        }
    }
}
