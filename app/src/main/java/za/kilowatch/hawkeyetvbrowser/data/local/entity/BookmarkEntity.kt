package za.kilowatch.hawkeyetvbrowser.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String,
    val favicon: ByteArray? = null,
    val folderId: String? = null,
    val isFolder: Boolean = false,
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookmarkEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
