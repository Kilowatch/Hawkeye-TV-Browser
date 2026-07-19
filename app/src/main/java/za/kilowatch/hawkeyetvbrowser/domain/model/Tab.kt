package za.kilowatch.hawkeyetvbrowser.domain.model

import android.graphics.Bitmap
import java.util.UUID

data class Tab(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "about:blank",
    val title: String = "",
    val favicon: Bitmap? = null,
    val isIncognito: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis()
)
