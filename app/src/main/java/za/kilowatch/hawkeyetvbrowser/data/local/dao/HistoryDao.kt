package za.kilowatch.hawkeyetvbrowser.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import za.kilowatch.hawkeyetvbrowser.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY visitedAt DESC LIMIT :limit OFFSET :offset")
    fun getHistory(limit: Int = 200, offset: Int = 0): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history ORDER BY visitedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY visitedAt DESC")
    fun searchHistory(query: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearAll()

    @Query("DELETE FROM history WHERE visitedAt < :before")
    suspend fun clearOlderThan(before: Long)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM history")
    suspend fun count(): Int
}
