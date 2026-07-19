package za.kilowatch.hawkeyetvbrowser.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.domain.model.Tab

@Composable
fun TabManagerScreen(
    onTabSelected: () -> Unit,
    onNavigateToBrowser: () -> Unit,
    tabViewModel: TabViewModel = hiltViewModel()
) {
    val tabs by tabViewModel.tabs.collectAsState()
    val activeTabId by tabViewModel.activeTabId.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.tabs_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                tabViewModel.createNewTab()
                onNavigateToBrowser()
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text(stringResource(R.string.tabs_new_tab))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tabs, key = { it.id }) { tab ->
                TabCard(
                    tab = tab,
                    isActive = tab.id == activeTabId,
                    onSelect = {
                        tabViewModel.selectTab(tab.id)
                        onTabSelected()
                    },
                    onClose = { tabViewModel.closeTab(tab.id) }
                )
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: Tab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = tab.title.ifBlank { stringResource(R.string.tab_new_title) },
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = tab.url.ifBlank { "about:blank" },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (tab.isIncognito) {
                Text(
                    text = stringResource(R.string.tab_incognito_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.clear_data_clear))
            }
        }
    }
}
