package za.kilowatch.hawkeyetvbrowser.core.util

import android.net.Uri

object UrlValidator {

    fun isValidUrl(url: String): Boolean {
        return try {
            val uri = Uri.parse(url)
            uri.scheme != null && uri.host != null
        } catch (_: Exception) {
            false
        }
    }

    fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }
        return "https://$trimmed"
    }

    fun extractDomain(url: String): String {
        return try {
            Uri.parse(url).host ?: url
        } catch (_: Exception) {
            url
        }
    }
}
