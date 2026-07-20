package za.kilowatch.hawkeyetvbrowser.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.core.downloader.DownloadProgress
import za.kilowatch.hawkeyetvbrowser.core.downloader.DownloadStatus

@Composable
fun DownloadProgressDialog(
    progress: DownloadProgress,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
    onInstallApk: (String) -> Unit
) {
    val title = when (progress.status) {
        DownloadStatus.DOWNLOADING -> stringResource(R.string.download_dialog_downloading)
        DownloadStatus.COMPLETED -> stringResource(R.string.download_dialog_completed)
        DownloadStatus.FAILED -> stringResource(R.string.download_dialog_failed)
        DownloadStatus.CANCELLED -> stringResource(R.string.download_dialog_cancelled)
    }

    val isApk = progress.fileName.lowercase().endsWith(".apk")

    AlertDialog(
        onDismissRequest = {
            if (progress.status == DownloadStatus.DOWNLOADING) {
                onCancel()
            } else {
                onDismiss()
            }
        },
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = progress.fileName,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                when (progress.status) {
                    DownloadStatus.DOWNLOADING -> {
                        if (progress.progressPercent >= 0) {
                            LinearProgressIndicator(
                                progress = { progress.progressPercent / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val percentText = if (progress.progressPercent >= 0) {
                            "${progress.progressPercent}%"
                        } else {
                            stringResource(R.string.download_dialog_downloading_status)
                        }

                        val sizeText = if (progress.totalBytes > 0) {
                            "${formatBytes(progress.bytesDownloaded)} / ${formatBytes(progress.totalBytes)}"
                        } else {
                            formatBytes(progress.bytesDownloaded)
                        }

                        Text(
                            text = "$percentText ($sizeText)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    DownloadStatus.COMPLETED -> {
                        val sizeText = formatBytes(progress.bytesDownloaded)
                        Text(
                            text = stringResource(R.string.download_dialog_saved_successfully, sizeText),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    DownloadStatus.FAILED -> {
                        Text(
                            text = progress.errorMessage ?: stringResource(R.string.download_dialog_unknown_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    DownloadStatus.CANCELLED -> {
                        Text(
                            text = stringResource(R.string.download_dialog_cancelled_msg),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            when (progress.status) {
                DownloadStatus.COMPLETED -> {
                    if (isApk && progress.filePath != null) {
                        TextButton(onClick = { onInstallApk(progress.filePath) }) {
                            Text(stringResource(R.string.downloader_install_apk))
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.download_dialog_close))
                        }
                    }
                }
                DownloadStatus.DOWNLOADING -> {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(R.string.downloader_cancel))
                    }
                }
                else -> {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.download_dialog_close))
                    }
                }
            }
        },
        dismissButton = {
            if (progress.status == DownloadStatus.COMPLETED && isApk) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.download_dialog_dismiss))
                }
            }
        }
    )
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.2f MB", mb)
        kb >= 1.0 -> String.format("%.2f KB", kb)
        else -> "$bytes B"
    }
}
