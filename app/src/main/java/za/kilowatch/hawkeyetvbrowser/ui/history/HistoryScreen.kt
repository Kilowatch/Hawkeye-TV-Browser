package za.kilowatch.hawkeyetvbrowser.ui.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.domain.model.HistoryEntry
import za.kilowatch.hawkeyetvbrowser.ui.browser.PremiumAmberBackground

@Composable
fun HistoryScreen(
    onEntryOpened: (String) -> Unit,
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val history by historyViewModel.history.collectAsState()
    val searchQuery by historyViewModel.searchQuery.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        PremiumAmberBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TvNavIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.history_back),
                        onClick = onNavigateBack
                    )

                    Text(
                        text = stringResource(R.string.history_title).uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFFFFF8E1)
                    )
                }

                TvPillActionButton(
                    text = stringResource(R.string.history_clear_all),
                    icon = Icons.Default.Delete,
                    onClick = { historyViewModel.clearAll() },
                    containerColor = Color(0xFFD32F2F).copy(alpha = 0.85f),
                    contentColor = Color.White
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { historyViewModel.search(it) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.history_search),
                        color = Color(0xFFFFF8E1).copy(alpha = 0.4f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFFFFB300)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFB300),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color(0xFF19110B).copy(alpha = 0.95f),
                    unfocusedContainerColor = Color(0xFF19110B).copy(alpha = 0.8f),
                    focusedTextColor = Color(0xFFFFF8E1),
                    unfocusedTextColor = Color(0xFFFFF8E1)
                )
            )

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.history_empty),
                        fontSize = 16.sp,
                        color = Color(0xFFFFF8E1).copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    history.forEach { (groupLabel, entries) ->
                        item {
                            Text(
                                text = groupLabel.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Color(0xFFFFB300),
                                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp, start = 4.dp)
                            )
                        }
                        items(entries, key = { it.id }) { entry ->
                            TvHistoryItemCard(
                                entry = entry,
                                onOpen = { onEntryOpened(entry.url) },
                                onDelete = { historyViewModel.deleteEntry(entry.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvHistoryItemCard(
    entry: HistoryEntry,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.02f else 1.0f,
        label = "HistoryScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (showHighlight) Color(0xFF22160C) else Color(0xFF19110B).copy(alpha = 0.95f))
            .border(
                1.dp,
                if (showHighlight) Color(0xFFFFB300) else Color(0xFFFFB300).copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onOpen
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = stringResource(R.string.desc_web_icon),
                    tint = Color(0xFFFFF8E1).copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title ?: entry.url,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFF8E1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.url,
                    fontSize = 12.sp,
                    color = Color(0xFFFFF8E1).copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        TvDeleteIconButton(onClick = onDelete)
    }
}

@Composable
private fun TvDeleteIconButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.15f else 1.0f,
        label = "DeleteScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .size(34.dp)
            .clip(CircleShape)
            .background(if (showHighlight) Color(0xFFD32F2F) else Color.White.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.clear_data_clear),
            tint = if (showHighlight) Color.White else Color(0xFFFFF8E1).copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun TvNavIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.1f else 1.0f,
        label = "NavIconScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .size(44.dp)
            .clip(CircleShape)
            .background(if (showHighlight) Color(0xFFFFB300).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f))
            .border(1.dp, if (showHighlight) Color(0xFFFFB300) else Color.White.copy(alpha = 0.12f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (showHighlight) Color(0xFFFFB300) else Color(0xFFFFF8E1),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TvPillActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.06f else 1.0f,
        label = "PillActionScale"
    )

    Row(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, if (showHighlight) Color.White else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
