package za.kilowatch.hawkeyetvbrowser.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.domain.model.HistoryEntry
import za.kilowatch.hawkeyetvbrowser.domain.usecase.HistoryUseCase
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCase: HistoryUseCase
) : ViewModel() {

    private val _history = MutableStateFlow<Map<String, List<HistoryEntry>>>(emptyMap())
    val history: StateFlow<Map<String, List<HistoryEntry>>> = _history.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadHistory()
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                historyUseCase.getHistoryGrouped().collect { _history.value = it }
            } else {
                historyUseCase.searchHistory(query).collect { entries ->
                    _history.value = mapOf("search_results" to entries)
                }
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyUseCase.clearAll()
            _history.value = emptyMap()
        }
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            historyUseCase.deleteEntry(id)
        }
    }

    /**
     * Maps internal date group keys to human-readable labels using string resources.
     */
    fun getGroupLabel(context: Context, key: String): String {
        return when (key) {
            "today" -> context.getString(R.string.history_today)
            "yesterday" -> context.getString(R.string.history_yesterday)
            "last_7_days" -> context.getString(R.string.history_last_7_days)
            "last_30_days" -> context.getString(R.string.history_last_30_days)
            "search_results" -> context.getString(R.string.history_search_results)
            else -> {
                // Parse "yyyy_MM" format
                try {
                    val parts = key.split("_")
                    if (parts.size == 2) {
                        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        val date = SimpleDateFormat("yyyy_MM", Locale.getDefault()).parse(key)
                        date?.let { dateFormat.format(it) } ?: key
                    } else key
                } catch (_: Exception) { key }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            historyUseCase.getHistoryGrouped().collect { grouped ->
                _history.value = grouped
            }
        }
    }
}
