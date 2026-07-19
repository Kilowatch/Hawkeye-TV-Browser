package za.kilowatch.hawkeyetvbrowser.core.downloader

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloaderCodeValidator @Inject constructor() {

    fun classifyDestination(url: String): DestinationType {
        val lowerUrl = url.lowercase()
        return when {
            lowerUrl.endsWith(".apk") -> DestinationType.APK_FILE
            lowerUrl.endsWith(".zip") || lowerUrl.endsWith(".rar") -> DestinationType.ARCHIVE
            lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mkv") -> DestinationType.VIDEO
            lowerUrl.contains("github.com") || lowerUrl.contains("gitlab.com") -> DestinationType.SOURCE_CODE
            else -> DestinationType.WEBPAGE
        }
    }

    fun isTrustedSource(url: String): Boolean {
        val trustedDomains = listOf(
            "github.com", "gitlab.com", "apkmirror.com",
            "f-droid.org", "xda-developers.com", "troypoint.com"
        )
        val host = Uri.parse(url).host?.lowercase() ?: return false
        return trustedDomains.any { host.endsWith(it) }
    }
}

enum class DestinationType {
    APK_FILE,
    ARCHIVE,
    VIDEO,
    SOURCE_CODE,
    WEBPAGE
}
