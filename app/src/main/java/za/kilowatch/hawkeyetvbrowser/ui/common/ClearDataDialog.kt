package za.kilowatch.hawkeyetvbrowser.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ClearDataOptions(
    val clearHistory: Boolean = false,
    val clearCache: Boolean = false,
    val clearCookies: Boolean = false
)

@Composable
fun ClearDataDialog(
    onConfirm: (ClearDataOptions) -> Unit,
    onDismiss: () -> Unit
) {
    var clearHistory by remember { mutableStateOf(false) }
    var clearCache by remember { mutableStateOf(false) }
    var clearCookies by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear Browsing Data") },
        text = {
            androidx.compose.foundation.layout.Column {
                DataCheckbox(
                    label = "Browsing History",
                    checked = clearHistory,
                    onCheckedChange = { clearHistory = it }
                )
                DataCheckbox(
                    label = "Cache",
                    checked = clearCache,
                    onCheckedChange = { clearCache = it }
                )
                DataCheckbox(
                    label = "Cookies",
                    checked = clearCookies,
                    onCheckedChange = { clearCookies = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(ClearDataOptions(clearHistory, clearCache, clearCookies))
                }
            ) {
                Text("Clear")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DataCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label)
    }
}
