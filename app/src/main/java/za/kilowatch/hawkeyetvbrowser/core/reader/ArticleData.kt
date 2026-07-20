package za.kilowatch.hawkeyetvbrowser.core.reader

data class ArticleData(
    val title: String,
    val byline: String,
    val contentHtml: String,
    val length: Int
) {
    val estimatedReadingTimeMinutes: Int
        get() = (length / 1000).coerceAtLeast(1)
}
