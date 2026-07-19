package za.kilowatch.hawkeyetvbrowser.domain.model

data class HistoryEntry(
    val id: String,
    val url: String,
    val title: String? = null,
    val favicon: ByteArray? = null,
    val visitedAt: Long = System.currentTimeMillis(),
    val dateGroup: String = ""
)
