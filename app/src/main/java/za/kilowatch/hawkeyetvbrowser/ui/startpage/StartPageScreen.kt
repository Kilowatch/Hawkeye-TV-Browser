package za.kilowatch.hawkeyetvbrowser.ui.startpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class QuickLink(
    val title: String,
    val url: String
)

private val defaultQuickLinks = listOf(
    QuickLink("Google", "https://www.google.com"),
    QuickLink("YouTube", "https://www.youtube.com"),
    QuickLink("Netflix", "https://www.netflix.com"),
    QuickLink("Reddit", "https://www.reddit.com"),
    QuickLink("Amazon", "https://www.amazon.com"),
    QuickLink("Twitch", "https://www.twitch.tv"),
    QuickLink("Wikipedia", "https://www.wikipedia.org")
)

@Composable
fun StartPageScreen(
    onUrlEntered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo / Title
        Text(
            text = "Hawkeye TV Browser",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 48.dp, bottom = 32.dp)
        )

        // URL Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(0.8f),
            placeholder = { Text("Search or enter URL") },
            singleLine = true
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Button(onClick = { onUrlEntered(searchText) }) {
                Text("Go")
            }
            Button(onClick = { searchText = "" }) {
                Text("Clear")
            }
        }

        // Quick Links
        Text(
            text = "Quick Links",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 32.dp, bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(defaultQuickLinks) { link ->
                QuickLinkCard(link = link, onClick = { onUrlEntered(link.url) })
            }
        }

        // Recently visited placeholder
        Text(
            text = "Recently Visited",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 32.dp, bottom = 8.dp)
        )

        Text(
            text = "Your recent sites will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickLinkCard(
    link: QuickLink,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = link.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
