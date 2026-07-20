package za.kilowatch.hawkeyetvbrowser.core.downloader

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class DownloadStatus {
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class DownloadProgress(
    val url: String,
    val fileName: String,
    val bytesDownloaded: Long = 0L,
    val totalBytes: Long = -1L,
    val progressPercent: Int = 0,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val filePath: String? = null,
    val errorMessage: String? = null
)

@Singleton
class FileDownloader @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val httpClient: OkHttpClient
) {
    private val downloadClient by lazy {
        httpClient.newBuilder()
            .followRedirects(true)
            .followSslRedirects(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    fun downloadFile(
        url: String,
        customFileName: String? = null,
        userAgent: String? = null,
        contentDisposition: String? = null,
        mimeType: String? = null
    ): Flow<DownloadProgress> = flow {
        val fileName = customFileName?.ifBlank { null }
            ?: extractFileName(url, contentDisposition, mimeType)
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: context.cacheDir
        val destinationFile = File(downloadDir, fileName)

        var currentProgress = DownloadProgress(
            url = url,
            fileName = fileName,
            status = DownloadStatus.DOWNLOADING
        )
        emit(currentProgress)

        val requestBuilder = Request.Builder().url(url)
        if (!userAgent.isNullOrBlank()) {
            requestBuilder.header("User-Agent", userAgent)
        } else {
            requestBuilder.header("User-Agent", "HawkeyeTVBrowser/1.0")
        }

        try {
            downloadClient.newCall(requestBuilder.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    emit(
                        currentProgress.copy(
                            status = DownloadStatus.FAILED,
                            errorMessage = "HTTP Error ${response.code}"
                        )
                    )
                    return@flow
                }

                val body = response.body ?: run {
                    emit(
                        currentProgress.copy(
                            status = DownloadStatus.FAILED,
                            errorMessage = "Empty response body"
                        )
                    )
                    return@flow
                }

                val totalBytes = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(destinationFile)

                val buffer = ByteArray(8192)
                var bytesDownloaded = 0L
                var bytesRead: Int
                var lastReportTime = System.currentTimeMillis()

                outputStream.use { out ->
                    inputStream.use { input ->
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            out.write(buffer, 0, bytesRead)
                            bytesDownloaded += bytesRead

                            val now = System.currentTimeMillis()
                            if (now - lastReportTime > 100 || (totalBytes > 0 && bytesDownloaded == totalBytes)) {
                                lastReportTime = now
                                val percent = if (totalBytes > 0) {
                                    ((bytesDownloaded * 100) / totalBytes).toInt().coerceIn(0, 100)
                                } else {
                                    -1
                                }
                                currentProgress = currentProgress.copy(
                                    bytesDownloaded = bytesDownloaded,
                                    totalBytes = totalBytes,
                                    progressPercent = percent
                                )
                                emit(currentProgress)
                            }
                        }
                    }
                }

                emit(
                    currentProgress.copy(
                        bytesDownloaded = bytesDownloaded,
                        totalBytes = if (totalBytes > 0) totalBytes else bytesDownloaded,
                        progressPercent = 100,
                        status = DownloadStatus.COMPLETED,
                        filePath = destinationFile.absolutePath
                    )
                )
            }
        } catch (e: Exception) {
            emit(
                currentProgress.copy(
                    status = DownloadStatus.FAILED,
                    errorMessage = e.localizedMessage ?: "Download error"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun extractFileName(
        url: String,
        contentDisposition: String? = null,
        mimeType: String? = null
    ): String {
        return try {
            val guessed = android.webkit.URLUtil.guessFileName(url, contentDisposition, mimeType)
            if (!guessed.isNullOrBlank() && guessed != "downloadfile" && guessed != "downloadfile.bin") {
                guessed
            } else {
                val uri = android.net.Uri.parse(url)
                val path = uri.path
                val lastSegment = path?.substringAfterLast('/')
                if (!lastSegment.isNullOrBlank() && lastSegment.contains(".")) {
                    lastSegment
                } else {
                    "download_${System.currentTimeMillis()}"
                }
            }
        } catch (_: Exception) {
            "download_${System.currentTimeMillis()}"
        }
    }
}
