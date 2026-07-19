package za.kilowatch.hawkeyetvbrowser.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.domain.model.HistoryEntry

@Composable
fun HistoryScreen(
    onEntryOpened: () -> Unit,
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val history by historyViewModel.history.collectAsState()
    val searchQuery by historyViewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { historyViewModel.clearAll() }) {
                    Text(stringResource(R.string.history_clear_all))
                }
                Button(onClick = onNavigateBack) {
                    Text(stringResource(R.string.history_back))
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { historyViewModel.search(it) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.history_search)) },
            singleLine = true
        )

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                history.forEach { (groupLabel, entries) ->
                    item {
                        Text(
                            text = groupLabel,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    items(entries, key = { it.id }) { entry ->
                        HistoryItem(
                            entry = entry,
                            onDelete = { historyViewModel.deleteEntry(entry.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    entry: HistoryEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title ?: entry.url,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.url,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onDelete) {
                Text(stringResource(R.string.clear_data_clear))
            }
        }
    }
}
