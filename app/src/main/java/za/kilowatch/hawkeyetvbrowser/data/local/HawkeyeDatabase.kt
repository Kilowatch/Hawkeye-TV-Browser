package za.kilowatch.hawkeyetvbrowser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import za.kilowatch.hawkeyetvbrowser.data.local.dao.BookmarkDao
import za.kilowatch.hawkeyetvbrowser.data.local.dao.HistoryDao
import za.kilowatch.hawkeyetvbrowser.data.local.entity.BookmarkEntity
import za.kilowatch.hawkeyetvbrowser.data.local.entity.HistoryEntity

@Database(
    entities = [
        BookmarkEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HawkeyeDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
}
