import SwiftUI

struct BookmarksView: View {
    @EnvironmentObject var bookmarkManager: BookmarkManager
    
    var body: some View {
        NavigationView {
            Group {
                if bookmarkManager.bookmarks.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "bookmark")
                            .font(.system(size: 64))
                            .foregroundColor(.gray)
                        Text("暂无书签")
                            .foregroundColor(.gray)
                    }
                } else {
                    List {
                        ForEach(bookmarkManager.bookmarks) { bookmark in
                            Button(action: {
                                // 打开书签
                            }) {
                                HStack {
                                    Image(systemName: "bookmark.fill")
                                        .foregroundColor(.blue)
                                    VStack(alignment: .leading) {
                                        Text(bookmark.title)
                                            .lineLimit(1)
                                        Text(bookmark.url)
                                            .font(.caption)
                                            .foregroundColor(.gray)
                                            .lineLimit(1)
                                    }
                                    Spacer()
                                    Button(action: {
                                        bookmarkManager.removeBookmark(url: bookmark.url)
                                    }) {
                                        Image(systemName: "trash")
                                            .foregroundColor(.red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("书签")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
