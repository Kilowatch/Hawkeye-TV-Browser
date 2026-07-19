package za.kilowatch.hawkeyetvbrowser.core.downloader

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import za.kilowatch.hawkeyetvbrowser.domain.usecase.InputResolution
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolves numeric Downloader codes (aftv.news shortcodes) to their destination URLs.
 */
@Singleton
class DownloaderCodeResolver @Inject constructor(
    private val httpClient: OkHttpClient
) {
    companion object {
        const val AFTV_BASE_URL = "https://aftv.news/"
        const val TIMEOUT_SECONDS = 10L
        const val MIN_CODE_LENGTH = 1
        const val MAX_CODE_LENGTH = 8
    }

    private val resolveClient: OkHttpClient by lazy {
        httpClient.newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    suspend fun resolve(code: String): InputResolution = withContext(Dispatchers.IO) {
        if (!isValidCode(code)) {
            return@withContext InputResolution.DownloaderCodeError(
                code = code,
                reason = "Invalid code format. Must be 1 to 8 digits."
            )
        }

        try {
            val resolveRequest = Request.Builder()
                .url("${AFTV_BASE_URL}${code}")
                .head()
                .header("User-Agent", "HawkeyeTVBrowser/1.0")
                .build()

            resolveClient.newCall(resolveRequest).execute().use { resolveResponse ->
                val statusCode = resolveResponse.code

                if (statusCode !in listOf(301, 302, 303, 307, 308)) {
                    val reason = when (statusCode) {
                        404 -> "Code '$code' was not found. It may have expired or never existed."
                        in 500..599 -> "The aftv.news service is temporarily unavailable. Try again later."
                        else -> "Unexpected response (HTTP $statusCode) from aftv.news."
                    }
                    return@withContext InputResolution.DownloaderCodeError(code, reason)
                }

                val destinationUrl = resolveResponse.header("Location")
                if (destinationUrl.isNullOrBlank()) {
                    return@withContext InputResolution.DownloaderCodeError(
                        code = code,
                        reason = "Code resolved but no destination URL was provided."
                    )
                }

                val uri = Uri.parse(destinationUrl)
                if (uri.scheme !in listOf("http", "https")) {
                    return@withContext InputResolution.DownloaderCodeError(
                        code = code,
                        reason = "Destination URL uses an unsupported scheme: ${uri.scheme}"
                    )
                }

                return@withContext InputResolution.DownloaderCode(
                    code = code,
                    resolvedUrl = destinationUrl,
                    destinationHost = uri.host ?: "unknown"
                )
            }

        } catch (e: SocketTimeoutException) {
            return@withContext InputResolution.DownloaderCodeError(
                code = code,
                reason = "Connection timed out. Check your internet connection."
            )
        } catch (e: UnknownHostException) {
            return@withContext InputResolution.DownloaderCodeError(
                code = code,
                reason = "Cannot reach aftv.news. Check your internet connection."
            )
        } catch (e: IOException) {
            return@withContext InputResolution.DownloaderCodeError(
                code = code,
                reason = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }

    private fun isValidCode(code: String): Boolean {
        return code.length in MIN_CODE_LENGTH..MAX_CODE_LENGTH && code.all { it.isDigit() }
    }
}
