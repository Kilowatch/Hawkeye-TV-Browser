package za.kilowatch.hawkeyetvbrowser.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import za.kilowatch.hawkeyetvbrowser.data.local.entity.BookmarkEntity

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE folderId IS NULL ORDER BY position ASC")
    fun getRootBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE folderId = :folderId ORDER BY position ASC")
    fun getBookmarksInFolder(folderId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun findByUrl(url: String): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM bookmarks WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%'")
    fun searchBookmarks(query: String): Flow<List<BookmarkEntity>>
}
