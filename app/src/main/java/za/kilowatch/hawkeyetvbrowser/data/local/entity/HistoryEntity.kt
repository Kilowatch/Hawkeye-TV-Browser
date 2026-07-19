package za.kilowatch.hawkeyetvbrowser.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [
        Index(value = ["url"]),
        Index(value = ["visitedAt"])
    ]
)
data class HistoryEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val title: String? = null,
    val favicon: ByteArray? = null,
    val visitedAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HistoryEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
