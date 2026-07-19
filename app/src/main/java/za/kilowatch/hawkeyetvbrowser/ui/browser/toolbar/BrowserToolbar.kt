package za.kilowatch.hawkeyetvbrowser.ui.browser.toolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.path
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ripple
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import za.kilowatch.hawkeyetvbrowser.R

@Composable
fun BrowserToolbar(
    visible: Boolean,
    urlText: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isIncognito: Boolean,
    isDesktopMode: Boolean,
    isCursorMode: Boolean = true,
    onUrlChanged: (String) -> Unit,
    onGo: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onRefresh: () -> Unit,
    onStop: () -> Unit,
    onNewTab: () -> Unit,
    onNewIncognitoTab: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleIncognito: () -> Unit,
    onToggleCursorMode: () -> Unit = {},
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    webPageBackgroundColor: Color? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val accentColor = if (isIncognito) Color(0xFFD682FF) else Color(0xFF00F0FF)

    // Dynamic color matching
    val baseBg = webPageBackgroundColor ?: MaterialTheme.colorScheme.surface
    val isLightBg = baseBg.luminance() > 0.5f

    val targetBgColor = baseBg.copy(alpha = 0.92f)
    val animatedBgColor by animateColorAsState(targetValue = targetBgColor, label = "ToolbarBgColor")

    val targetContentColor = if (isLightBg) Color(0xFF151419) else Color(0xFFFFF8E1)
    val animatedContentColor by animateColorAsState(targetValue = targetContentColor, label = "ToolbarContentColor")

    val targetOutlineColor = if (isLightBg) Color(0x2B000000) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val animatedOutlineColor by animateColorAsState(targetValue = targetOutlineColor, label = "ToolbarOutlineColor")

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(250))
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Glassmorphic toolbar panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(34.dp))
                    .background(animatedBgColor)
                    .border(
                        width = 1.dp,
                        color = animatedOutlineColor,
                        shape = RoundedCornerShape(34.dp)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Navigation Actions
                ToolbarIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.browser_back),
                    onClick = onBack,
                    enabled = canGoBack,
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isCursorMode = isCursorMode
                )

                ToolbarIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.browser_forward),
                    onClick = onForward,
                    enabled = canGoForward,
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isCursorMode = isCursorMode
                )

                ToolbarIconButton(
                    icon = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = if (isLoading) stringResource(R.string.browser_stop) else stringResource(R.string.browser_refresh),
                    onClick = { if (isLoading) onStop() else onRefresh() },
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isCursorMode = isCursorMode
                )

                 // URL Field
                ToolbarUrlField(
                    value = urlText,
                    onValueChange = onUrlChanged,
                    onGo = onGo,
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isLightBg = isLightBg,
                    isCursorMode = isCursorMode,
                    modifier = Modifier.weight(1f)
                )

                // Zoom Out Button
                ToolbarIconButton(
                    icon = Icons_Default_ZoomOut,
                    contentDescription = "Zoom Out",
                    onClick = onZoomOut,
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isCursorMode = isCursorMode
                )

                // Zoom In Button
                ToolbarIconButton(
                    icon = Icons_Default_ZoomIn,
                    contentDescription = "Zoom In",
                    onClick = onZoomIn,
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isCursorMode = isCursorMode
                )

                // Menu Button
                Box {
                    ToolbarIconButton(
                        icon = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.browser_menu),
                        onClick = { showMenu = !showMenu },
                        isIncognito = isIncognito,
                        contentColor = animatedContentColor,
                        isCursorMode = isCursorMode
                    )

                    // Modern Dropdown Menu styling
                    MaterialTheme(
                        colorScheme = MaterialTheme.colorScheme.copy(
                            surface = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_new_tab)) },
                                onClick = { showMenu = false; onNewTab() },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_new_incognito_tab)) },
                                onClick = { showMenu = false; onNewIncognitoTab() },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (isDesktopMode) stringResource(R.string.menu_mobile_site)
                                        else stringResource(R.string.menu_desktop_site)
                                    )
                                },
                                onClick = { showMenu = false; onToggleDesktopMode() },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_bookmarks)) },
                                onClick = { showMenu = false; onOpenBookmarks() },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_history)) },
                                onClick = { showMenu = false; onOpenHistory() },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_settings)) },
                                onClick = { showMenu = false; onOpenSettings() },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    }
                }
            }

            // Progress bar (sleek thin neon line directly under the panel)
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .padding(horizontal = 34.dp, vertical = 0.dp),
                    color = accentColor,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun ToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isIncognito: Boolean = false,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    isCursorMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || (isFocused && !isCursorMode)

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ButtonScale"
    )

    val accentColor = if (isIncognito) Color(0xFFD682FF) else Color(0xFF00F0FF)

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.Transparent
            showHighlight -> accentColor.copy(alpha = 0.12f)
            else -> Color.Transparent
        },
        label = "ButtonBg"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            !enabled -> contentColor.copy(alpha = 0.25f)
            showHighlight -> accentColor
            else -> contentColor
        },
        label = "ButtonIcon"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> Color.Transparent
            showHighlight -> accentColor.copy(alpha = 0.40f)
            else -> contentColor.copy(alpha = 0.1f)
        },
        label = "ButtonBorder"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .size(46.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = accentColor),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ToolbarUrlField(
    value: String,
    onValueChange: (String) -> Unit,
    onGo: () -> Unit,
    isIncognito: Boolean = false,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    isLightBg: Boolean = false,
    isCursorMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || (isFocused && !isCursorMode)

    val accentColor = if (isIncognito) Color(0xFFD682FF) else Color(0xFF00F0FF)

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.015f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "FieldScale"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> accentColor
            isHovered -> accentColor.copy(alpha = 0.5f)
            isLightBg -> Color(0xFF000000).copy(alpha = 0.12f)
            else -> Color(0xFFFFFFFF).copy(alpha = 0.12f)
        },
        label = "FieldBorder"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isLightBg) {
            Color(0xFF000000).copy(alpha = 0.05f) // Subtle light-grey background
        } else {
            Color(0xFFFFFFFF).copy(alpha = 0.06f) // Subtle dark-grey background
        }
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .height(46.dp)
            .background(containerColor, RoundedCornerShape(23.dp))
            .border(1.dp, borderColor, RoundedCornerShape(23.dp)),
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = contentColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(onGo = { onGo() }),
        cursorBrush = SolidColor(accentColor),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_or_enter_url),
                        color = contentColor.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                innerTextField()
            }
        }
    )
}

val Icons_Default_ZoomIn = ImageVector.Builder(
    name = "ZoomIn",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        stroke = SolidColor(Color.White),
        strokeLineWidth = 2.2f,
        strokeLineCap = StrokeCap.Round
    ) {
        // Horizontal line
        moveTo(6f, 12f)
        lineTo(18f, 12f)
        // Vertical line
        moveTo(12f, 6f)
        lineTo(12f, 18f)
    }
}.build()

val Icons_Default_ZoomOut = ImageVector.Builder(
    name = "ZoomOut",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        stroke = SolidColor(Color.White),
        strokeLineWidth = 2.2f,
        strokeLineCap = StrokeCap.Round
    ) {
        // Horizontal line
        moveTo(6f, 12f)
        lineTo(18f, 12f)
    }
}.build()
