package za.kilowatch.hawkeyetvbrowser.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import za.kilowatch.hawkeyetvbrowser.data.local.entity.BookmarkEntity
import za.kilowatch.hawkeyetvbrowser.data.repository.BookmarkRepository
import za.kilowatch.hawkeyetvbrowser.domain.model.Bookmark
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    fun getBookmarksInFolder(folderId: String? = null): Flow<List<Bookmark>> {
        return bookmarkRepository.getBookmarksInFolder(folderId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun searchBookmarks(query: String): Flow<List<Bookmark>> {
        return bookmarkRepository.searchBookmarks(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun isBookmarked(url: String): Boolean = bookmarkRepository.isBookmarked(url)

    suspend fun addBookmark(title: String, url: String, favicon: ByteArray? = null, folderId: String? = null) {
        val entity = BookmarkEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            url = url,
            favicon = favicon,
            folderId = folderId,
            isFolder = false,
            createdAt = System.currentTimeMillis()
        )
        bookmarkRepository.addBookmark(entity)
    }

    suspend fun createFolder(name: String, parentFolderId: String? = null) {
        val entity = BookmarkEntity(
            id = UUID.randomUUID().toString(),
            title = name,
            url = "",
            isFolder = true,
            folderId = parentFolderId,
            createdAt = System.currentTimeMillis()
        )
        bookmarkRepository.addBookmark(entity)
    }

    suspend fun removeBookmark(id: String) {
        bookmarkRepository.deleteBookmark(id)
    }

    private fun BookmarkEntity.toDomain(): Bookmark = Bookmark(
        id = id,
        title = title,
        url = url,
        favicon = favicon,
        folderId = folderId,
        isFolder = isFolder,
        position = position,
        createdAt = createdAt
    )
}
