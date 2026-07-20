package za.kilowatch.hawkeyetvbrowser.data.repository

import kotlinx.coroutines.flow.Flow
import za.kilowatch.hawkeyetvbrowser.data.local.dao.BookmarkDao
import za.kilowatch.hawkeyetvbrowser.data.local.entity.BookmarkEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getRootBookmarks()

    fun getBookmarksInFolder(folderId: String?): Flow<List<BookmarkEntity>> =
        if (folderId.isNullOrBlank()) {
            bookmarkDao.getRootBookmarks()
        } else {
            bookmarkDao.getBookmarksInFolder(folderId)
        }

    fun searchBookmarks(query: String): Flow<List<BookmarkEntity>> =
        bookmarkDao.searchBookmarks(query)

    suspend fun findByUrl(url: String): BookmarkEntity? = bookmarkDao.findByUrl(url)

    suspend fun addBookmark(bookmark: BookmarkEntity) = bookmarkDao.insert(bookmark)

    suspend fun deleteBookmark(id: String) = bookmarkDao.deleteById(id)

    suspend fun isBookmarked(url: String): Boolean = bookmarkDao.findByUrl(url) != null
}
