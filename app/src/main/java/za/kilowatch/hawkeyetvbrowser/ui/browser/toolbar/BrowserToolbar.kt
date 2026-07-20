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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onOpenSettings: () -> Unit = {},
    onToggleIncognito: () -> Unit = {},
    onToggleCursorMode: () -> Unit = {},
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    onVoiceSearch: () -> Unit = {},
    onOpenReaderMode: () -> Unit = {},
    webPageBackgroundColor: Color? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val accentColor = if (isIncognito) Color(0xFFD682FF) else Color(0xFF00F0FF)

    // Dynamic color matching (matching Image 1b light frosted glass by default for start page & light sites)
    val isLightBg = webPageBackgroundColor == null || webPageBackgroundColor.luminance() > 0.5f
    val baseBg = webPageBackgroundColor ?: Color(0xFFF3F0F8)

    val targetBgColor = if (isLightBg) baseBg.copy(alpha = 0.95f) else Color(0xFF19110B).copy(alpha = 0.95f)
    val animatedBgColor by animateColorAsState(targetValue = targetBgColor, label = "ToolbarBgColor")

    val targetContentColor = if (isLightBg) Color(0xFF2B2735) else Color(0xFFFFF8E1)
    val animatedContentColor by animateColorAsState(targetValue = targetContentColor, label = "ToolbarContentColor")

    val targetOutlineColor = if (isLightBg) Color(0xFFE2DDD7) else Color(0xFFFFB300).copy(alpha = 0.25f)
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
            // Light Frosted Glass Toolbar Panel (Image 1b)
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

                // Voice Search Button
                ToolbarIconButton(
                    icon = Icons.Default.Mic,
                    contentDescription = stringResource(R.string.voice_search_mic_desc),
                    onClick = onVoiceSearch,
                    isIncognito = isIncognito,
                    contentColor = animatedContentColor,
                    isCursorMode = isCursorMode
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

                    // Options Dropdown Menu adapting dynamically to website background color
                    val menuBaseBg = webPageBackgroundColor ?: Color(0xFF19110B)
                    val menuIsLightBg = webPageBackgroundColor != null && webPageBackgroundColor.luminance() > 0.5f
                    val menuBgColor = if (menuIsLightBg) menuBaseBg.copy(alpha = 0.98f) else Color(0xFF19110B).copy(alpha = 0.96f)
                    val animatedMenuBgColor by animateColorAsState(targetValue = menuBgColor, label = "MenuBgColor")

                    val menuHeaderColor = if (isIncognito) Color(0xFFD682FF) else if (menuIsLightBg) Color(0xFFE65100) else Color(0xFFFFB300)
                    val menuBorderColor = if (isIncognito) Color(0xFFD682FF).copy(alpha = 0.5f) else if (menuIsLightBg) Color(0xFFE65100).copy(alpha = 0.35f) else Color(0xFFFFB300).copy(alpha = 0.5f)

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .width(300.dp)
                            .background(animatedMenuBgColor)
                            .border(
                                width = 1.5.dp,
                                color = menuBorderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .padding(8.dp)
                    ) {
                        // Header Title
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.menu_options_title).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = menuHeaderColor
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(menuHeaderColor.copy(alpha = 0.25f))
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        TvDropdownMenuItem(
                            text = stringResource(R.string.menu_new_tab),
                            icon = Icons.Default.Add,
                            onClick = { showMenu = false; onNewTab() },
                            badgeText = stringResource(R.string.menu_shortcut_new_tab),
                            isIncognito = isIncognito,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode
                        )

                        TvDropdownMenuItem(
                            text = stringResource(R.string.menu_new_incognito_tab),
                            icon = Icons.Default.Security,
                            onClick = { showMenu = false; onNewIncognitoTab() },
                            isIncognito = true,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode,
                            accentColorOverride = Color(0xFFD682FF)
                        )

                        TvDropdownMenuItem(
                            text = if (isDesktopMode) stringResource(R.string.menu_mobile_site) else stringResource(R.string.menu_desktop_site),
                            icon = if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.DesktopWindows,
                            onClick = { showMenu = false; onToggleDesktopMode() },
                            badgeText = if (isDesktopMode) stringResource(R.string.menu_badge_desktop) else stringResource(R.string.menu_badge_mobile),
                            isIncognito = isIncognito,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode
                        )

                        TvDropdownMenuItem(
                            text = stringResource(R.string.menu_reader_mode),
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            onClick = { showMenu = false; onOpenReaderMode() },
                            isIncognito = isIncognito,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode
                        )

                        TvDropdownMenuItem(
                            text = stringResource(R.string.menu_bookmarks),
                            icon = Icons.Default.Bookmark,
                            onClick = { showMenu = false; onOpenBookmarks() },
                            badgeText = stringResource(R.string.menu_shortcut_bookmark),
                            isIncognito = isIncognito,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode
                        )

                        TvDropdownMenuItem(
                            text = stringResource(R.string.menu_history),
                            icon = Icons.Default.History,
                            onClick = { showMenu = false; onOpenHistory() },
                            isIncognito = isIncognito,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode
                        )

                        TvDropdownMenuItem(
                            text = stringResource(R.string.menu_settings),
                            icon = Icons.Default.Settings,
                            onClick = { showMenu = false; onOpenSettings() },
                            isIncognito = isIncognito,
                            isLightBg = menuIsLightBg,
                            isCursorMode = isCursorMode
                        )
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
            else -> contentColor.copy(alpha = 0.15f)
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

    val activeAccentColor = if (isIncognito) Color(0xFFD682FF) else if (isLightBg) Color(0xFFE65100) else Color(0xFF00F0FF)

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.015f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "FieldScale"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> activeAccentColor
            isHovered -> activeAccentColor.copy(alpha = 0.6f)
            isLightBg -> Color(0xFFD0C9DB)
            else -> Color(0xFFFFB300).copy(alpha = 0.25f)
        },
        label = "FieldBorder"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isLightBg && (isFocused || isHovered) -> Color(0xFFDCD7E6)
            isLightBg -> Color(0xFFE4E0EC)
            isFocused || isHovered -> Color(0xFF000000).copy(alpha = 0.45f)
            else -> Color(0xFF000000).copy(alpha = 0.30f)
        },
        label = "FieldBg"
    )

    val textColor = if (isLightBg) Color(0xFF2B2735) else Color(0xFFFFF8E1)
    val placeholderColor = if (isLightBg) Color(0xFF656173) else Color(0xFFFFF8E1).copy(alpha = 0.5f)

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
            color = textColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(onGo = { onGo() }),
        cursorBrush = SolidColor(if (isLightBg) Color(0xFF2B2735) else activeAccentColor),
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
                        color = placeholderColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun TvDropdownMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    badgeText: String? = null,
    isIncognito: Boolean = false,
    isLightBg: Boolean = false,
    isCursorMode: Boolean = true,
    accentColorOverride: Color? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || (isFocused && !isCursorMode)
    val accentColor = accentColorOverride ?: if (isIncognito) Color(0xFFD682FF) else if (isLightBg) Color(0xFFE65100) else Color(0xFFFFB300)

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.04f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "MenuItemScale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            showHighlight -> accentColor.copy(alpha = if (isLightBg) 0.14f else 0.18f)
            else -> Color.Transparent
        },
        label = "MenuItemBg"
    )

    val defaultContentColor = if (isLightBg) Color(0xFF151419) else Color(0xFFFFF8E1)

    val contentColor by animateColorAsState(
        targetValue = when {
            showHighlight -> accentColor
            else -> defaultContentColor.copy(alpha = 0.90f)
        },
        label = "MenuItemContent"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            showHighlight -> accentColor.copy(alpha = 0.45f)
            else -> Color.Transparent
        },
        label = "MenuItemBorder"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (showHighlight) {
                            accentColor.copy(alpha = 0.15f)
                        } else if (isLightBg) {
                            Color.Black.copy(alpha = 0.05f)
                        } else {
                            Color.White.copy(alpha = 0.05f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = text,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = if (showHighlight) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (showHighlight) {
                            accentColor.copy(alpha = 0.25f)
                        } else if (isLightBg) {
                            Color.Black.copy(alpha = 0.06f)
                        } else {
                            Color.White.copy(alpha = 0.08f)
                        }
                    )
                    .border(
                        1.dp,
                        if (showHighlight) {
                            accentColor.copy(alpha = 0.5f)
                        } else if (isLightBg) {
                            Color.Black.copy(alpha = 0.12f)
                        } else {
                            Color.White.copy(alpha = 0.12f)
                        },
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    color = if (showHighlight) accentColor else defaultContentColor.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
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
