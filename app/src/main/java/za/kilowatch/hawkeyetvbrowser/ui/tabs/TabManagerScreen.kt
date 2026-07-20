package za.kilowatch.hawkeyetvbrowser.ui.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
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
import za.kilowatch.hawkeyetvbrowser.domain.model.Tab
import za.kilowatch.hawkeyetvbrowser.ui.browser.PremiumAmberBackground

@Composable
fun TabManagerScreen(
    onTabSelected: () -> Unit,
    onNavigateToBrowser: () -> Unit,
    tabViewModel: TabViewModel = hiltViewModel()
) {
    val tabs by tabViewModel.tabs.collectAsState()
    val activeTabId by tabViewModel.activeTabId.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        PremiumAmberBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
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
                        onClick = onNavigateToBrowser
                    )

                    Column {
                        Text(
                            text = stringResource(R.string.tabs_title).uppercase(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFFFFF8E1)
                        )
                        Text(
                            text = "${tabs.size} ${if (tabs.size == 1) "TAB" else "TABS"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFB300),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvPillActionButton(
                        text = stringResource(R.string.tabs_new_tab),
                        icon = Icons.Default.Add,
                        onClick = {
                            tabViewModel.createNewTab()
                            onNavigateToBrowser()
                        },
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color.Black
                    )

                    TvPillActionButton(
                        text = stringResource(R.string.menu_new_incognito_tab),
                        icon = Icons.Default.Security,
                        onClick = {
                            tabViewModel.createIncognitoTab()
                            onNavigateToBrowser()
                        },
                        containerColor = Color(0xFFD682FF),
                        contentColor = Color.Black
                    )
                }
            }

            // Tabs Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tabs, key = { it.id }) { tab ->
                    TvTabCard(
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
}

@Composable
private fun TvTabCard(
    tab: Tab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused
    val accentColor = if (tab.isIncognito) Color(0xFFD682FF) else Color(0xFFFFB300)

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.05f else 1.0f,
        label = "TabCardScale"
    )

    val containerBg by animateColorAsState(
        targetValue = when {
            isActive -> Color(0xFF22160C)
            showHighlight -> Color(0xFF1F140A)
            else -> Color(0xFF150D06).copy(alpha = 0.95f)
        },
        label = "TabCardBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isActive -> Color(0xFF00E5FF)
            showHighlight -> accentColor
            else -> Color(0xFFFFB300).copy(alpha = 0.15f)
        },
        label = "TabCardBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(20.dp))
            .background(containerBg)
            .border(
                width = if (isActive || showHighlight) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = tab.title.ifBlank { stringResource(R.string.tab_new_title) },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFF8E1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = tab.url.ifBlank { "about:blank" },
                        fontSize = 12.sp,
                        color = Color(0xFFFFF8E1).copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                TvCloseIconButton(onClick = onClose)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF),
                            letterSpacing = 0.5.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (tab.isIncognito) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFD682FF).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFD682FF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.tab_incognito_label).uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD682FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvCloseIconButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.15f else 1.0f,
        label = "CloseScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .size(28.dp)
            .clip(CircleShape)
            .background(if (showHighlight) Color(0xFFD32F2F) else Color.White.copy(alpha = 0.08f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.clear_data_clear),
            tint = if (showHighlight) Color.White else Color(0xFFFFF8E1).copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp)
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
