package za.kilowatch.hawkeyetvbrowser.ui.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.ui.browser.PremiumAmberBackground
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
    val dohProvider by settingsViewModel.dohProvider.collectAsState()
    val isAdBlockEnabled by settingsViewModel.isAdBlockEnabled.collectAsState()
    val isSafeBrowsingEnabled by settingsViewModel.isSafeBrowsingEnabled.collectAsState()
    val isHighContrastEnabled by settingsViewModel.isHighContrastEnabled.collectAsState()
    val cookiePolicy by settingsViewModel.cookiePolicy.collectAsState()
    val isJavaScriptEnabled by settingsViewModel.isJavaScriptEnabled.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mahogany Amber / Honey Obsidian dark background
        PremiumAmberBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TvIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.settings_back_to_browser),
                    onClick = onNavigateBack
                )

                Text(
                    text = stringResource(R.string.settings_title).uppercase(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFFFFF8E1)
                )
            }

            // Search Engine Section
            SettingsSectionCard(title = stringResource(R.string.settings_search_engine)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    settingsViewModel.searchEngines.forEach { engine ->
                        val isSelected = selectedEngine.id == engine.id
                        TvPillButton(
                            text = searchEngineName(engine.id),
                            isSelected = isSelected,
                            onClick = { settingsViewModel.setSearchEngine(engine) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Display Section
            SettingsSectionCard(title = stringResource(R.string.settings_display)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_text_size),
                        color = Color(0xFFFFF8E1),
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${textZoom}%",
                        color = Color(0xFFFFB300),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = textZoom.toFloat(),
                    onValueChange = { settingsViewModel.setTextZoom(it.toInt()) },
                    valueRange = 50f..200f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFB300),
                        activeTrackColor = Color(0xFFFFB300),
                        inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                TvSettingsToggle(
                    label = stringResource(R.string.settings_desktop_mode),
                    checked = isDesktopMode,
                    onToggle = { settingsViewModel.toggleDesktopMode() }
                )
            }

            // Security & Privacy Section
            SettingsSectionCard(title = stringResource(R.string.settings_privacy)) {
                TvSettingsToggle(
                    label = stringResource(R.string.settings_adblock_title),
                    checked = isAdBlockEnabled,
                    onToggle = { settingsViewModel.toggleAdBlock() }
                )

                TvSettingsToggle(
                    label = stringResource(R.string.safebrowsing_warning_title),
                    checked = isSafeBrowsingEnabled,
                    onToggle = { settingsViewModel.toggleSafeBrowsing() }
                )

                TvSettingsToggle(
                    label = stringResource(R.string.settings_do_not_track),
                    checked = isDntEnabled,
                    onToggle = { settingsViewModel.toggleDnt() }
                )

                TvSettingsToggle(
                    label = stringResource(R.string.settings_block_popups),
                    checked = isPopupBlocking,
                    onToggle = { settingsViewModel.togglePopupBlocking() }
                )

                TvSettingsToggle(
                    label = stringResource(R.string.settings_javascript),
                    checked = isJavaScriptEnabled,
                    onToggle = { settingsViewModel.toggleJavaScript() }
                )
            }

            // Secure DNS (DoH) Section
            SettingsSectionCard(title = stringResource(R.string.settings_doh_title)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val providers = listOf(
                        "system" to stringResource(R.string.doh_provider_off),
                        "cloudflare" to stringResource(R.string.doh_provider_cloudflare),
                        "google" to stringResource(R.string.doh_provider_google),
                        "adguard" to stringResource(R.string.doh_provider_adguard)
                    )
                    providers.forEach { (id, name) ->
                        TvPillButton(
                            text = name,
                            isSelected = dohProvider == id,
                            onClick = { settingsViewModel.setDohProvider(id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Cookie Controls Section
            SettingsSectionCard(title = stringResource(R.string.settings_cookie_policy)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val policies = listOf(
                        "allow_all" to stringResource(R.string.cookie_allow_all),
                        "block_third_party" to stringResource(R.string.cookie_block_third_party),
                        "block_all" to stringResource(R.string.cookie_block_all)
                    )
                    policies.forEach { (id, name) ->
                        TvPillButton(
                            text = name,
                            isSelected = cookiePolicy == id,
                            onClick = { settingsViewModel.setCookiePolicy(id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Accessibility Suite Section
            SettingsSectionCard(title = stringResource(R.string.settings_accessibility_title)) {
                TvSettingsToggle(
                    label = stringResource(R.string.settings_high_contrast),
                    checked = isHighContrastEnabled,
                    onToggle = { settingsViewModel.toggleHighContrast() }
                )
            }

            // Cursor Configuration Section
            SettingsSectionCard(title = stringResource(R.string.settings_cursor)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_cursor_speed),
                        color = Color(0xFFFFF8E1),
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${cursorSpeed.toInt()}",
                        color = Color(0xFFFFB300),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = cursorSpeed,
                    onValueChange = { settingsViewModel.setCursorSpeed(it) },
                    valueRange = 3f..30f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFB300),
                        activeTrackColor = Color(0xFFFFB300),
                        inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }

            // Clear Data & Danger Zone Section
            SettingsSectionCard(title = stringResource(R.string.settings_data)) {
                TvActionButton(
                    text = stringResource(R.string.settings_clear_data),
                    onClick = { showClearDialog = true },
                    containerColor = Color(0xFFD32F2F).copy(alpha = 0.85f),
                    contentColor = Color.White
                )
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

            // About Section
            SettingsSectionCard(title = stringResource(R.string.settings_about)) {
                Text(
                    text = stringResource(R.string.settings_version),
                    color = Color(0xFFFFF8E1).copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = Color(0xFFFFB300),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF19110B).copy(alpha = 0.95f))
                .border(
                    width = 1.dp,
                    color = Color(0xFFFFB300).copy(alpha = 0.18f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun TvSettingsToggle(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.02f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ToggleScale"
    )

    val containerColor by animateColorAsState(
        targetValue = if (showHighlight) Color(0xFFFFB300).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f),
        label = "ToggleBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (showHighlight) Color(0xFFFFB300).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.08f),
        label = "ToggleBorder"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFFFF8E1),
            fontSize = 15.sp,
            fontWeight = if (showHighlight) FontWeight.SemiBold else FontWeight.Normal
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (checked) Color(0xFFFFB300) else Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TvPillButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight || isSelected) 1.05f else 1.0f,
        label = "PillScale"
    )

    val bg by animateColorAsState(
        targetValue = when {
            isSelected -> Color(0xFFFFB300)
            showHighlight -> Color(0xFFFFB300).copy(alpha = 0.25f)
            else -> Color.White.copy(alpha = 0.05f)
        },
        label = "PillBg"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.Black
            showHighlight -> Color(0xFFFFD54F)
            else -> Color(0xFFFFF8E1).copy(alpha = 0.8f)
        },
        label = "PillText"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(
                1.dp,
                if (isSelected || showHighlight) Color(0xFFFFB300) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected || showHighlight) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun TvIconButton(
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
        label = "IconScale"
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
private fun TvActionButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = Color(0xFFFFB300),
    contentColor: Color = Color.Black
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || isFocused

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.03f else 1.0f,
        label = "ActionScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(
                1.dp,
                if (showHighlight) Color.White else Color.Transparent,
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
