package za.kilowatch.hawkeyetvbrowser.domain.model

data class Bookmark(
    val id: String,
    val title: String,
    val url: String,
    val favicon: ByteArray? = null,
    val folderId: String? = null,
    val isFolder: Boolean = false,
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
