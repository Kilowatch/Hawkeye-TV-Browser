package za.kilowatch.hawkeyetvbrowser.domain.usecase

import za.kilowatch.hawkeyetvbrowser.core.downloader.DownloaderCodeResolver
import za.kilowatch.hawkeyetvbrowser.core.util.UrlValidator
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchUseCase @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val downloaderCodeResolver: DownloaderCodeResolver
) {
    private val numericCodePattern = Regex("^\\d{1,8}$")

    suspend fun resolveInput(input: String): InputResolution {
        val trimmed = input.trim()

        return when {
            numericCodePattern.matches(trimmed) -> {
                downloaderCodeResolver.resolve(trimmed)
            }

            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> {
                InputResolution.DirectUrl(trimmed)
            }

            trimmed.contains(".") && !trimmed.contains(" ") -> {
                InputResolution.DirectUrl(UrlValidator.normalizeUrl(trimmed))
            }

            else -> {
                val engine = settingsRepo.getSearchEngine()
                val encodedQuery = try {
                    URLEncoder.encode(trimmed, "UTF-8")
                } catch (_: java.io.UnsupportedEncodingException) {
                    trimmed
                }
                val searchUrl = engine.searchUrlTemplate.replace(
                    "{searchTerms}", encodedQuery
                )
                InputResolution.SearchQuery(searchUrl, trimmed)
            }
        }
    }
}
