import SwiftUI

struct SourceCodeView: View {
    let sourceCode: String
    let url: String
    
    @Environment(\.presentationMode) var presentationMode
    @State private var searchText = ""
    @State private var showShareSheet = false
    @State private var isWordWrap = true
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 搜索栏
                SearchBar(text: $searchText, placeholder: "搜索源码")
                    .padding(.horizontal)
                    .padding(.vertical, 8)
                
                // 源码显示
                ScrollView {
                    if isWordWrap {
                        Text(attributedSourceCode)
                            .font(.system(size: 12, design: .monospaced))
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding()
                    } else {
                        ScrollView(.horizontal, showsIndicators: true) {
                            Text(attributedSourceCode)
                                .font(.system(size: 12, design: .monospaced))
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding()
                        }
                    }
                }
                .background(Color(.systemBackground))
            }
            .navigationTitle("网页源码")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button(action: { isWordWrap.toggle() }) {
                            Label(isWordWrap ? "关闭自动换行" : "开启自动换行", systemImage: "text.append")
                        }
                        
                        Button(action: {
                            UIPasteboard.general.string = sourceCode
                        }) {
                            Label("复制全部", systemImage: "doc.on.doc")
                        }
                        
                        Button(action: { showShareSheet = true }) {
                            Label("分享", systemImage: "square.and.arrow.up")
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                            .foregroundColor(.blue)
                    }
                }
            }
        }
    }
    
    private var attributedSourceCode: AttributedString {
        var attributed = AttributedString(sourceCode)
        attributed.font = .system(size: 12, design: .monospaced)
        return attributed
    }
}
