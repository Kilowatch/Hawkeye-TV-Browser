package za.kilowatch.hawkeyetvbrowser.domain.model

import androidx.annotation.StringRes

data class SearchEngine(
    val id: String,
    @StringRes val nameResId: Int,
    val searchUrlTemplate: String
) {
    companion object {
        val GOOGLE = SearchEngine(
            id = "google",
            nameResId = -1, // Will be replaced with actual string resource
            searchUrlTemplate = "https://www.google.com/search?q={searchTerms}"
        )
        val BING = SearchEngine(
            id = "bing",
            nameResId = -1,
            searchUrlTemplate = "https://www.bing.com/search?q={searchTerms}"
        )
        val YAHOO = SearchEngine(
            id = "yahoo",
            nameResId = -1,
            searchUrlTemplate = "https://search.yahoo.com/search?p={searchTerms}"
        )
        val DUCKDUCKGO = SearchEngine(
            id = "duckduckgo",
            nameResId = -1,
            searchUrlTemplate = "https://duckduckgo.com/?q={searchTerms}"
        )

        val DEFAULTS = listOf(GOOGLE, BING, YAHOO, DUCKDUCKGO)

        fun fromId(id: String): SearchEngine {
            return DEFAULTS.find { it.id == id } ?: GOOGLE
        }
    }
}
