package za.kilowatch.hawkeyetvbrowser.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import za.kilowatch.hawkeyetvbrowser.data.local.dao.HistoryDao
import za.kilowatch.hawkeyetvbrowser.data.local.entity.HistoryEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    fun searchHistory(query: String): Flow<List<HistoryEntity>> =
        historyDao.searchHistory(query)

    fun getHistoryGrouped(): Flow<Map<String, List<HistoryEntity>>> {
        return historyDao.getAllHistory().map { entries ->
            entries.groupBy { entity ->
                getDateGroupKey(entity.visitedAt)
            }
        }
    }

    suspend fun recordVisit(url: String, title: String?, favicon: ByteArray?) {
        val entity = HistoryEntity(
            id = java.util.UUID.randomUUID().toString(),
            url = url,
            title = title,
            favicon = favicon,
            visitedAt = System.currentTimeMillis()
        )
        historyDao.insert(entity)
    }

    suspend fun clearAll() = historyDao.clearAll()
    suspend fun clearOlderThan(before: Long) = historyDao.clearOlderThan(before)
    suspend fun deleteById(id: String) = historyDao.deleteById(id)

    /**
     * Returns a string key for date grouping.
     * UI layer should map these to localized strings via stringResource().
     */
    private fun getDateGroupKey(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        calendar.time = Date(timestamp)

        return when {
            isSameDay(calendar, today) -> "today"
            isYesterday(calendar, today) -> "yesterday"
            isWithinDays(calendar, today, 7) -> "last_7_days"
            isWithinDays(calendar, today, 30) -> "last_30_days"
            else -> {
                val formatter = SimpleDateFormat("yyyy_MM", Locale.getDefault())
                formatter.format(calendar.time)
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(cal: Calendar, today: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            time = today.time
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(cal, yesterday)
    }

    private fun isWithinDays(cal: Calendar, today: Calendar, days: Int): Boolean {
        val threshold = Calendar.getInstance().apply {
            time = today.time
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return cal.after(threshold) && !isSameDay(cal, today) && !isYesterday(cal, today)
    }
}
