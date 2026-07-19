package za.kilowatch.hawkeyetvbrowser.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import za.kilowatch.hawkeyetvbrowser.data.repository.HistoryRepository
import za.kilowatch.hawkeyetvbrowser.domain.model.HistoryEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    fun getHistory(): Flow<List<HistoryEntry>> {
        return historyRepository.getAllHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getHistoryGrouped(): Flow<Map<String, List<HistoryEntry>>> {
        return historyRepository.getHistoryGrouped().map { grouped ->
            grouped.mapValues { (_, entries) ->
                entries.map { it.toDomain() }
            }
        }
    }

    fun searchHistory(query: String): Flow<List<HistoryEntry>> {
        return historyRepository.searchHistory(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun recordVisit(url: String, title: String?) {
        historyRepository.recordVisit(url, title, null)
    }

    suspend fun clearAll() = historyRepository.clearAll()

    suspend fun deleteEntry(id: String) = historyRepository.deleteById(id)

    private fun za.kilowatch.hawkeyetvbrowser.data.local.entity.HistoryEntity.toDomain(): HistoryEntry {
        return HistoryEntry(
            id = id,
            url = url,
            title = title,
            favicon = favicon,
            visitedAt = visitedAt
        )
    }
}
