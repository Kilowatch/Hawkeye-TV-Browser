package za.kilowatch.hawkeyetvbrowser.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DownloaderCodeDialog(
    code: String,
    resolvedUrl: String,
    destinationHost: String,
    isApk: Boolean,
    isTrusted: Boolean,
    onOpen: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Downloader Code: $code") },
        text = {
            Text(
                buildString {
                    appendLine("This code resolves to:")
                    appendLine(resolvedUrl)
                    appendLine()
                    appendLine("Host: $destinationHost")
                    if (isTrusted) {
                        appendLine("Source: Trusted")
                    } else {
                        appendLine("Source: Unknown - exercise caution")
                    }
                    if (isApk) {
                        appendLine()
                        appendLine("WARNING: This is an APK file.")
                        append("Installing apps from outside the Play Store")
                        append(" may pose a security risk.")
                    }
                }.trimEnd()
            )
        },
        confirmButton = {
            Button(onClick = onOpen) {
                Text(if (isApk) "Install APK" else "Open")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
