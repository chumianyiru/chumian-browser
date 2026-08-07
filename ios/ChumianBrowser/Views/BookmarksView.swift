import SwiftUI

struct BookmarksView: View {
    @EnvironmentObject var bookmarkManager: BookmarkManager
    @Environment(\.presentationMode) var presentationMode
    
    let onSelect: (String) -> Void
    
    @State private var searchText = ""
    @State private var selectedFolder = "默认"
    @State private var showAddFolder = false
    @State private var newFolderName = ""
    @State private var showEditBookmark = false
    @State private var editingBookmark: Bookmark?
    
    var filteredBookmarks: [Bookmark] {
        let bookmarks = bookmarkManager.bookmarksInFolder(selectedFolder)
        if searchText.isEmpty {
            return bookmarks
        }
        return bookmarks.filter {
            $0.title.lowercased().contains(searchText.lowercased()) ||
            $0.url.lowercased().contains(searchText.lowercased())
        }
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // 搜索栏
                SearchBar(text: $searchText, placeholder: "搜索书签")
                    .padding(.horizontal)
                    .padding(.vertical, 8)
                
                // 文件夹选择
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(bookmarkManager.folders, id: \.self) { folder in
                            FolderChip(
                                name: folder,
                                isSelected: folder == selectedFolder,
                                count: bookmarkManager.bookmarksInFolder(folder).count
                            ) {
                                selectedFolder = folder
                            }
                        }
                        
                        Button(action: { showAddFolder = true }) {
                            HStack(spacing: 4) {
                                Image(systemName: "plus")
                                Text("新建文件夹")
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(Color(.systemGray6))
                            .foregroundColor(.blue)
                            .cornerRadius(16)
                        }
                    }
                    .padding(.horizontal)
                }
                .padding(.bottom, 8)
                
                // 书签列表
                if filteredBookmarks.isEmpty {
                    VStack(spacing: 16) {
                        Spacer()
                        Image(systemName: "bookmark")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("暂无书签")
                            .foregroundColor(.gray)
                        Spacer()
                    }
                } else {
                    List {
                        ForEach(filteredBookmarks) { bookmark in
                            Button(action: {
                                onSelect(bookmark.url)
                                presentationMode.wrappedValue.dismiss()
                            }) {
                                HStack(spacing: 12) {
                                    // 网站图标占位
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(Color.blue.opacity(0.1))
                                        Text(String(bookmark.title.prefix(1)).uppercased())
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.blue)
                                    }
                                    .frame(width: 32, height: 32)
                                    
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(bookmark.title)
                                            .font(.system(size: 15))
                                            .foregroundColor(.primary)
                                            .lineLimit(1)
                                        
                                        Text(bookmark.url)
                                            .font(.system(size: 12))
                                            .foregroundColor(.gray)
                                            .lineLimit(1)
                                    }
                                    
                                    Spacer()
                                    
                                    Image(systemName: "chevron.right")
                                        .foregroundColor(.gray)
                                        .font(.system(size: 12))
                                }
                                .padding(.vertical, 4)
                            }
                            .contextMenu {
                                Button(action: {
                                    editingBookmark = bookmark
                                    showEditBookmark = true
                                }) {
                                    Label("编辑", systemImage: "pencil")
                                }
                                
                                Button(action: {
                                    bookmarkManager.removeBookmark(bookmark)
                                }) {
                                    Label("删除", systemImage: "trash")
                                }
                            }
                        }
                        .onDelete { offsets in
                            let bookmarksToDelete = offsets.map { filteredBookmarks[$0] }
                            bookmarksToDelete.forEach { bookmarkManager.removeBookmark($0) }
                        }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            .navigationTitle("书签")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("完成") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .alert("新建文件夹", isPresented: $showAddFolder) {
                TextField("文件夹名称", text: $newFolderName)
                Button("取消", role: .cancel) {
                    newFolderName = ""
                }
                Button("创建") {
                    if !newFolderName.trimmingCharacters(in: .whitespaces).isEmpty {
                        bookmarkManager.addFolder(newFolderName.trimmingCharacters(in: .whitespaces))
                        newFolderName = ""
                    }
                }
            }
            .sheet(isPresented: $showEditBookmark) {
                if let bookmark = editingBookmark {
                    EditBookmarkView(bookmark: bookmark) { title, url, folder in
                        bookmarkManager.updateBookmark(bookmark, title: title, url: url, folder: folder)
                    }
                }
            }
        }
    }
}

struct FolderChip: View {
    let name: String
    let isSelected: Bool
    let count: Int
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                Image(systemName: "folder.fill")
                    .font(.system(size: 12))
                Text(name)
                    .font(.system(size: 13))
                Text("(\(count))")
                    .font(.system(size: 12))
                    .opacity(0.7)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(isSelected ? Color.blue : Color(.systemGray6))
            .foregroundColor(isSelected ? .white : .primary)
            .cornerRadius(16)
        }
    }
}

struct SearchBar: UIViewRepresentable {
    @Binding var text: String
    var placeholder: String
    
    func makeUIView(context: Context) -> UISearchBar {
        let searchBar = UISearchBar(frame: .zero)
        searchBar.placeholder = placeholder
        searchBar.delegate = context.coordinator
        searchBar.searchBarStyle = .minimal
        return searchBar
    }
    
    func updateUIView(_ uiView: UISearchBar, context: Context) {
        uiView.text = text
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UISearchBarDelegate {
        let parent: SearchBar
        
        init(_ parent: SearchBar) {
            self.parent = parent
        }
        
        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            parent.text = searchText
        }
    }
}

struct EditBookmarkView: View {
    let bookmark: Bookmark
    let onSave: (String, String, String) -> Void
    
    @Environment(\.presentationMode) var presentationMode
    @State private var title: String
    @State private var url: String
    @State private var folder: String
    
    @EnvironmentObject var bookmarkManager: BookmarkManager
    
    init(bookmark: Bookmark, onSave: @escaping (String, String, String) -> Void) {
        self.bookmark = bookmark
        self.onSave = onSave
        _title = State(initialValue: bookmark.title)
        _url = State(initialValue: bookmark.url)
        _folder = State(initialValue: bookmark.folder)
    }
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("书签信息")) {
                    TextField("标题", text: $title)
                    TextField("网址", text: $url)
                        .autocapitalization(.none)
                        .disableAutocorrection(true)
                }
                
                Section(header: Text("文件夹")) {
                    Picker("文件夹", selection: $folder) {
                        ForEach(bookmarkManager.folders, id: \.self) { folder in
                            Text(folder).tag(folder)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                }
            }
            .navigationTitle("编辑书签")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("取消") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("保存") {
                        onSave(title, url, folder)
                        presentationMode.wrappedValue.dismiss()
                    }
                    .fontWeight(.bold)
                }
            }
        }
    }
}
