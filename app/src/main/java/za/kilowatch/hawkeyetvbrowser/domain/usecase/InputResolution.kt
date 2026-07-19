package za.kilowatch.hawkeyetvbrowser.domain.usecase

/**
 * Sealed class representing the resolved result of user input from the URL bar.
 */
sealed class InputResolution {
    /** User typed a direct URL */
    data class DirectUrl(val url: String) : InputResolution()

    /** User typed a search query */
    data class SearchQuery(val url: String, val originalQuery: String) : InputResolution()

    /** User typed a numeric Downloader code */
    data class DownloaderCode(
        val code: String,
        val resolvedUrl: String,
        val destinationHost: String
    ) : InputResolution()

    /** Numeric code could not be resolved */
    data class DownloaderCodeError(
        val code: String,
        val reason: String
    ) : InputResolution()
}
