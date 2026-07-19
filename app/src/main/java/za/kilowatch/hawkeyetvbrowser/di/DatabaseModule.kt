package za.kilowatch.hawkeyetvbrowser.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import za.kilowatch.hawkeyetvbrowser.data.local.HawkeyeDatabase
import za.kilowatch.hawkeyetvbrowser.data.local.dao.BookmarkDao
import za.kilowatch.hawkeyetvbrowser.data.local.dao.HistoryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HawkeyeDatabase {
        return Room.databaseBuilder(
            context,
            HawkeyeDatabase::class.java,
            "hawkeye_browser.db"
        ).build()
    }

    @Provides
    fun provideBookmarkDao(database: HawkeyeDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    fun provideHistoryDao(database: HawkeyeDatabase): HistoryDao {
        return database.historyDao()
    }
}
