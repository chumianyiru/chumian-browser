import SwiftUI

struct DownloadsView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                Image(systemName: "arrow.down.circle")
                    .font(.system(size: 64))
                    .foregroundColor(.blue)
                
                Text("下载管理")
                    .font(.title2)
                
                Text("使用系统下载管理器管理下载文件")
                    .font(.body)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                
                Button(action: {
                    // 打开系统下载管理器
                }) {
                    HStack {
                        Image(systemName: "square.and.arrow.up")
                        Text("打开系统下载管理器")
                    }
                }
                .buttonStyle(.borderedProminent)
            }
            .padding()
            .navigationTitle("下载管理")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
