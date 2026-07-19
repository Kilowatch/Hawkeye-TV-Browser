package za.kilowatch.hawkeyetvbrowser.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.domain.model.SearchEngine
import za.kilowatch.hawkeyetvbrowser.ui.common.ClearDataDialog

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val textZoom by settingsViewModel.textZoom.collectAsState()
    val isDesktopMode by settingsViewModel.isDesktopMode.collectAsState()
    val isDntEnabled by settingsViewModel.isDntEnabled.collectAsState()
    val isPopupBlocking by settingsViewModel.isPopupBlocking.collectAsState()
    val selectedEngine by settingsViewModel.selectedEngine.collectAsState()
    val cursorSpeed by settingsViewModel.cursorSpeed.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SettingsSection(title = stringResource(R.string.settings_search_engine)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                settingsViewModel.searchEngines.forEach { engine ->
                    Button(
                        onClick = { settingsViewModel.setSearchEngine(engine) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(searchEngineName(engine.id))
                    }
                }
            }
        }

        SettingsSection(title = stringResource(R.string.settings_display)) {
            Text(stringResource(R.string.settings_text_size) + ": ${textZoom}%")
            Slider(
                value = textZoom.toFloat(),
                onValueChange = { settingsViewModel.setTextZoom(it.toInt()) },
                valueRange = 50f..200f,
                steps = 15
            )
            SettingsToggle(
                label = stringResource(R.string.settings_desktop_mode),
                checked = isDesktopMode,
                onToggle = { settingsViewModel.toggleDesktopMode() }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_privacy)) {
            SettingsToggle(
                label = stringResource(R.string.settings_do_not_track),
                checked = isDntEnabled,
                onToggle = { settingsViewModel.toggleDnt() }
            )
            SettingsToggle(
                label = stringResource(R.string.settings_block_popups),
                checked = isPopupBlocking,
                onToggle = { settingsViewModel.togglePopupBlocking() }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_cursor)) {
            Text(stringResource(R.string.settings_cursor_speed) + ": ${cursorSpeed.toInt()}")
            Slider(
                value = cursorSpeed,
                onValueChange = { settingsViewModel.setCursorSpeed(it) },
                valueRange = 3f..30f,
                steps = 9
            )
        }

        SettingsSection(title = stringResource(R.string.settings_data)) {
            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.settings_clear_data))
            }
        }

        if (showClearDialog) {
            ClearDataDialog(
                onConfirm = { options ->
                    settingsViewModel.clearBrowsingData(options)
                    showClearDialog = false
                },
                onDismiss = { showClearDialog = false }
            )
        }

        SettingsSection(title = stringResource(R.string.settings_about)) {
            Text(stringResource(R.string.settings_version))
        }

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.settings_back_to_browser))
        }
    }
}

private fun searchEngineName(id: String): String {
    return when (id) {
        "google" -> "Google"
        "bing" -> "Bing"
        "yahoo" -> "Yahoo"
        "duckduckgo" -> "DuckDuckGo"
        else -> id
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
