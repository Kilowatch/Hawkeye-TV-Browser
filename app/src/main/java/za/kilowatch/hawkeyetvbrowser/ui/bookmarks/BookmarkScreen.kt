package za.kilowatch.hawkeyetvbrowser.ui.bookmarks

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
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
import za.kilowatch.hawkeyetvbrowser.domain.model.Bookmark
import za.kilowatch.hawkeyetvbrowser.ui.browser.PremiumAmberBackground

@Composable
fun BookmarkScreen(
    onBookmarkOpened: (String) -> Unit,
    onNavigateBack: () -> Unit,
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState()
    val breadcrumbs by bookmarkViewModel.breadcrumbs.collectAsState()
    var showNewFolderDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        PremiumAmberBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            // Header Row
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
                        contentDescription = stringResource(R.string.bookmarks_back),
                        onClick = onNavigateBack
                    )

                    Column {
                        Text(
                            text = stringResource(R.string.bookmarks_title).uppercase(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFFFFF8E1)
                        )

                        // Breadcrumbs trail
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            breadcrumbs.forEachIndexed { index, crumb ->
                                val isLast = index == breadcrumbs.size - 1
                                Text(
                                    text = if (crumb.second == "Root") stringResource(R.string.bookmark_root_folder) else crumb.second,
                                    fontSize = 13.sp,
                                    color = if (isLast) Color(0xFFFFB300) else Color(0xFFFFF8E1).copy(alpha = 0.5f),
                                    fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.clickable(enabled = !isLast) {
                                        if (index == 0) {
                                            bookmarkViewModel.navigateUp()
                                        }
                                    }
                                )
                                if (!isLast) {
                                    Text(" > ", fontSize = 13.sp, color = Color(0xFFFFF8E1).copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (breadcrumbs.size > 1) {
                        TvPillActionButton(
                            text = stringResource(R.string.bookmarks_back),
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = { bookmarkViewModel.navigateUp() },
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color(0xFFFFF8E1)
                        )
                    }

                    TvPillActionButton(
                        text = stringResource(R.string.bookmark_new_folder),
                        icon = Icons.Default.Add,
                        onClick = { showNewFolderDialog = true },
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color.Black
                    )
                }
            }

            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.bookmarks_empty),
                        fontSize = 16.sp,
                        color = Color(0xFFFFF8E1).copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bookmarks, key = { it.id }) { item ->
                        if (item.isFolder) {
                            FolderItemCard(
                                folder = item,
                                onClick = { bookmarkViewModel.navigateToFolder(item.id, item.title) },
                                onDelete = { bookmarkViewModel.deleteBookmark(item.id) }
                            )
                        } else {
                            BookmarkItemCard(
                                bookmark = item,
                                onOpen = { onBookmarkOpened(item.url) },
                                onDelete = { bookmarkViewModel.deleteBookmark(item.id) }
                            )
                        }
                    }
                }
            }
        }

        // New Folder Dialog
        if (showNewFolderDialog) {
            NewFolderDialog(
                onDismiss = { showNewFolderDialog = false },
                onCreate = { name ->
                    bookmarkViewModel.createFolder(name)
                    showNewFolderDialog = false
                }
            )
        }
    }
}

@Composable
private fun FolderItemCard(
    folder: Bookmark,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.03f else 1.0f,
        label = "FolderScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (showHighlight) Color(0xFF22160C) else Color(0xFF19110B).copy(alpha = 0.95f))
            .border(
                1.dp,
                if (showHighlight) Color(0xFFFFB300) else Color(0xFFFFB300).copy(alpha = 0.18f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
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
                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = stringResource(R.string.desc_folder_icon),
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = folder.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFF8E1)
            )
        }

        TvDeleteIconButton(onClick = onDelete)
    }
}

@Composable
private fun BookmarkItemCard(
    bookmark: Bookmark,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.03f else 1.0f,
        label = "BookmarkScale"
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
                    text = bookmark.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFF8E1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bookmark.url,
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
            contentDescription = stringResource(R.string.bookmarks_delete),
            tint = if (showHighlight) Color.White else Color(0xFFFFF8E1).copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun NewFolderDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.bookmark_new_folder),
                color = Color(0xFFFFF8E1),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text(stringResource(R.string.bookmark_folder_name)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFB300),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedLabelColor = Color(0xFFFFB300),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onCreate(folderName) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black)
            ) {
                Text(stringResource(R.string.bookmark_create), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.downloader_cancel), color = Color(0xFFFFF8E1).copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF19110B)
    )
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
