import SwiftUI

struct TabsView: View {
    @EnvironmentObject var tabManager: TabManager
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            ScrollView {
                LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                    ForEach(tabManager.tabs) { tab in
                        TabCard(tab: tab, isActive: tab.id == tabManager.activeTabId) {
                            tabManager.selectTab(tab.id)
                            presentationMode.wrappedValue.dismiss()
                        } onClose: {
                            tabManager.closeTab(tab.id)
                        }
                    }
                    
                    // 新建标签页按钮
                    Button(action: {
                        tabManager.addTab()
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        VStack {
                            Image(systemName: "plus")
                                .font(.system(size: 30))
                                .foregroundColor(.blue)
                            Text("新建标签页")
                                .font(.caption)
                                .foregroundColor(.blue)
                        }
                        .frame(height: 150)
                        .frame(maxWidth: .infinity)
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                }
                .padding()
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("标签页 (\(tabManager.tabCount))")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("关闭全部") {
                        tabManager.closeAllTabs()
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.red)
                }
            }
        }
    }
}

struct TabCard: View {
    let tab: BrowserTab
    let isActive: Bool
    let onSelect: () -> Void
    let onClose: () -> Void
    
    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(alignment: .leading, spacing: 8) {
                // 标题
                Text(tab.title)
                    .font(.system(size: 14, weight: .medium))
                    .lineLimit(1)
                    .foregroundColor(.primary)
                
                // URL
                Text(tab.url)
                    .font(.system(size: 12))
                    .lineLimit(2)
                    .foregroundColor(.gray)
                
                Spacer()
                
                // 底部预览占位
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color(.systemGray5))
                    .frame(height: 60)
            }
            .padding(10)
            .frame(height: 150)
            .frame(maxWidth: .infinity)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isActive ? Color.blue : Color.clear, lineWidth: 2)
            )
            .onTapGesture(perform: onSelect)
            
            // 关闭按钮
            Button(action: onClose) {
                Image(systemName: "xmark.circle.fill")
                    .foregroundColor(.gray)
                    .font(.system(size: 18))
                    .background(Color(.systemBackground).clipShape(Circle()))
            }
            .padding(6)
        }
    }
}
