package za.kilowatch.hawkeyetvbrowser.ui.browser

import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.ui.browser.toolbar.BrowserToolbar
import za.kilowatch.hawkeyetvbrowser.ui.common.CursorOverlay
import za.kilowatch.hawkeyetvbrowser.ui.common.DownloadProgressDialog
import za.kilowatch.hawkeyetvbrowser.ui.main.MainViewModel
import za.kilowatch.hawkeyetvbrowser.ui.theme.DefaultCursorColor

@Composable
fun BrowserScreen(
    mainViewModel: MainViewModel,
    onNavigateToTabs: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    browserViewModel: BrowserViewModel = hiltViewModel()
) {
    val state by browserViewModel.state.collectAsState()
    val pendingCode by browserViewModel.pendingDownloaderCode.collectAsState()
    val downloadProgress by browserViewModel.downloadProgress.collectAsState()
    val activeTab by browserViewModel.activeTab.collectAsState()
    val context = LocalContext.current

    // Get the active tab's WebView through the ViewModel
    val webView = remember(activeTab.id) {
        mainViewModel.tabManagementUseCase.getOrCreateWebView(activeTab.id)
    }

    // Apply WebView clients when WebView reference changes
    LaunchedEffect(webView) {
        webView?.let { browserViewModel.setupWebViewClients(it) }
    }

    // Wire cursor click to the root window DecorView so touches hit Compose
    // toolbar buttons and the URL field, not just the underlying WebView.
    val rootView = LocalView.current
    DisposableEffect(rootView) {
        val decorView = rootView.rootView
        browserViewModel.inputManager.cursorController.onClickAt = { x, y ->
            decorView.post {
                val downTime = SystemClock.uptimeMillis()
                val downEvent = MotionEvent.obtain(
                    downTime, downTime,
                    MotionEvent.ACTION_DOWN, x, y, 0
                )
                decorView.dispatchTouchEvent(downEvent)
                downEvent.recycle()

                val upEvent = MotionEvent.obtain(
                    downTime, downTime + 50,
                    MotionEvent.ACTION_UP, x, y, 0
                )
                decorView.dispatchTouchEvent(upEvent)
                upEvent.recycle()
            }
        }
        onDispose {
            // Reset to no-op when leaving the screen
            browserViewModel.inputManager.cursorController.onClickAt = { _, _ -> }
        }
    }

    // Dynamic hover simulation: dispatch ACTION_HOVER_MOVE to allow
    // Compose components and WebView links to trigger hover states.
    LaunchedEffect(state.cursorX, state.cursorY) {
        if (state.isCursorMode) {
            val decorView = rootView.rootView
            val time = SystemClock.uptimeMillis()
            val hoverEvent = MotionEvent.obtain(
                time, time,
                MotionEvent.ACTION_HOVER_MOVE, state.cursorX, state.cursorY, 0
            )
            hoverEvent.source = android.view.InputDevice.SOURCE_MOUSE
            decorView.dispatchGenericMotionEvent(hoverEvent)
            hoverEvent.recycle()
        }
    }

    // Dynamic toolbar height details feed to CursorController
    val density = LocalDensity.current
    val toolbarHeightPx = with(density) { 92.dp.toPx() }
    LaunchedEffect(state.toolbarVisible, toolbarHeightPx) {
        browserViewModel.inputManager.cursorController.toolbarVisible = state.toolbarVisible
        browserViewModel.inputManager.cursorController.toolbarHeight = toolbarHeightPx
    }

    val isStartPage = state.currentUrl.isBlank() || state.currentUrl == "about:blank"
    val webPageBg = state.webPageBackgroundColor ?: MaterialTheme.colorScheme.background
    val animatedScreenBg by animateColorAsState(
        targetValue = if (isStartPage) Color.Transparent else webPageBg,
        animationSpec = tween(400),
        label = "ScreenBgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedScreenBg)
            .onSizeChanged { size ->
                browserViewModel.inputManager.cursorController.updateViewSize(
                    size.width, size.height
                )
            }
            .onPreviewKeyEvent { event ->
                val ke = event.nativeKeyEvent ?: return@onPreviewKeyEvent false
                if (ke.action != KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent false
                if (ke.isCtrlPressed || ke.isAltPressed) {
                    handleKeyboardShortcut(ke, browserViewModel, mainViewModel)
                    return@onPreviewKeyEvent true
                }
                // Back button: navigate web history or reset to Start Page before allowing app exit
                if (ke.keyCode == KeyEvent.KEYCODE_BACK) {
                    if (browserViewModel.handleBackPressed()) {
                        return@onPreviewKeyEvent true
                    }
                    return@onPreviewKeyEvent false
                }
                // In cursor mode, intercept D-pad BEFORE children (toolbar buttons) get it
                if (state.isCursorMode && browserViewModel.handleDpadEvent(ke.keyCode, webView)) {
                    return@onPreviewKeyEvent true
                }
                false
            }
    ) {
        if (isStartPage) {
            // Render full screen premium start page background behind the Column
            PremiumAmberBackground()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            if (isStartPage) {
                // Logo & app name centered above the toolbar
                StartPageHeader(
                    isIncognito = state.isIncognito,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 54.dp, bottom = 12.dp)
                )
            }

            // Toolbar: always aligned at the top (under logo on start page, otherwise at top of screen)
            BrowserToolbar(
                visible = state.toolbarVisible,
                urlText = state.searchText,
                isLoading = state.isLoading,
                canGoBack = state.canGoBack,
                canGoForward = state.canGoForward,
                isIncognito = state.isIncognito,
                isDesktopMode = state.isDesktopMode,
                webPageBackgroundColor = if (isStartPage) null else state.webPageBackgroundColor,
                onUrlChanged = { browserViewModel.updateSearchText(it) },
                onGo = { browserViewModel.onUrlEntered(state.searchText) },
                onBack = { browserViewModel.onBackPressed() },
                onForward = { browserViewModel.onForwardPressed() },
                onRefresh = { browserViewModel.onRefresh() },
                onStop = { browserViewModel.onStopLoading() },
                onZoomIn = { browserViewModel.zoomIn() },
                onZoomOut = { browserViewModel.zoomOut() },
                onNewTab = {
                    browserViewModel.createNewTab()
                    browserViewModel.showToolbar()
                },
                onNewIncognitoTab = {
                    browserViewModel.createIncognitoTab()
                    browserViewModel.showToolbar()
                },
                onToggleDesktopMode = { browserViewModel.toggleDesktopMode() },
                onOpenBookmarks = onNavigateToBookmarks,
                onOpenHistory = onNavigateToHistory,
                onOpenSettings = onNavigateToSettings,
                onToggleIncognito = { browserViewModel.toggleIncognito() },
                isCursorMode = state.isCursorMode,
                modifier = Modifier.fillMaxWidth()
            )

            // Content Area below the toolbar (WebView when browsing, Search Engines on start page)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                if (!isStartPage) {
                    webView?.let { wv ->
                        AndroidView(
                            factory = { wv },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Start Page Search Engines list (directly below the toolbar)
                    SearchEnginesList(
                        onEngineClicked = { engine ->
                            val url = when (engine.id) {
                                "google" -> "https://www.google.com"
                                "bing" -> "https://www.bing.com"
                                "yahoo" -> "https://search.yahoo.com"
                                "duckduckgo" -> "https://duckduckgo.com"
                                else -> "https://www.google.com"
                            }
                            browserViewModel.onUrlEntered(url)
                        },
                        isCursorMode = state.isCursorMode,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                }
            }
        }

        // Cursor Overlay (top layer — renders above toolbar so it's visible everywhere)
        CursorOverlay(
            cursorX = state.cursorX,
            cursorY = state.cursorY,
            cursorColor = DefaultCursorColor,
            visible = state.isCursorMode,
            topScrollZoneEnabled = true, // Permanently active, boundary adjusts automatically
            toolbarVisible = state.toolbarVisible,
            toolbarHeight = toolbarHeightPx
        )

        // Downloader code confirmation dialog
        pendingCode?.let { info ->
            val typeLabel = when (info.destinationType) {
                za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType.APK_FILE -> "APK"
                za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType.ARCHIVE -> "Archive"
                za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType.VIDEO -> "Video"
                za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType.SOURCE_CODE -> "Source"
                za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType.WEBPAGE -> "Webpage"
            }
            val isApk = info.destinationType == za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType.APK_FILE

            AlertDialog(
                onDismissRequest = { browserViewModel.onDownloaderCodeDismissed() },
                title = {
                    Text(
                        stringResource(R.string.downloader_code_title) +
                        ": ${info.code}"
                    )
                },
                text = {
                    Text(
                        buildString {
                            appendLine(stringResource(R.string.downloader_code_resolves_to))
                            appendLine(info.resolvedUrl)
                            appendLine()
                            appendLine("${stringResource(R.string.downloader_code_host)}: ${info.destinationHost}")
                            appendLine("Type: $typeLabel")
                            if (info.isTrusted) {
                                appendLine(stringResource(R.string.downloader_source_trusted))
                            } else {
                                appendLine(stringResource(R.string.downloader_source_unknown))
                            }
                            if (isApk) {
                                appendLine()
                                appendLine(stringResource(R.string.downloader_apk_warning))
                            }
                        }.trimEnd()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        browserViewModel.onDownloaderCodeConfirmed(info.resolvedUrl)
                    }) {
                        Text(
                            if (isApk) stringResource(R.string.downloader_install_apk)
                            else stringResource(R.string.downloader_open)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { browserViewModel.onDownloaderCodeDismissed() }
                    ) {
                        Text(stringResource(R.string.downloader_cancel))
                    }
                }
            )
        }

        // Live download progress dialog for APKs and all file downloads
        downloadProgress?.let { progress ->
            DownloadProgressDialog(
                progress = progress,
                onCancel = { browserViewModel.cancelDownload() },
                onDismiss = { browserViewModel.dismissDownloadDialog() },
                onInstallApk = { filePath ->
                    browserViewModel.installApk(context, filePath)
                }
            )
        }
    }
}

@Composable
fun PremiumAmberBackground(modifier: Modifier = Modifier) {
    // A clean, premium gradient blending from mahogany-amber to dark honey obsidian
    val baseBackgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF200F00), // Very dark amber-brown
            Color(0xFF0C0600)  // Deep obsidian honey
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBackgroundBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // A single very soft, large ambient glow centered behind the top logo area
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x28FFB300), Color.Transparent),
                    center = Offset(size.width * 0.5f, -size.height * 0.1f),
                    radius = size.width * 0.75f
                ),
                size = size
            )
        }
    }
}

@Composable
fun StartPageHeader(
    isIncognito: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Logo card with 24dp rounded corners and amber gradient border
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .size(108.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFB300), Color(0xFFFF6D00))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.hawkeye_tv_logo_amber),
                contentDescription = "Hawkeye Amber Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Title and Subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "HAWKEYE TV BROWSER",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Color(0xFFFFF8E1), // Warm ivory
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xFFFFB300).copy(alpha = 0.5f),
                        offset = Offset(0f, 4f),
                        blurRadius = 14f
                    )
                )
            )

            Text(
                text = "Fast. Secure. Custom-built for TV.",
                fontSize = 13.sp,
                color = Color(0xFFFFF8E1).copy(alpha = 0.35f),
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun SearchEnginesList(
    onEngineClicked: (za.kilowatch.hawkeyetvbrowser.domain.model.SearchEngine) -> Unit,
    isCursorMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val engines = za.kilowatch.hawkeyetvbrowser.domain.model.SearchEngine.DEFAULTS

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            engines.forEach { engine ->
                val displayName = when (engine.id) {
                    "google" -> "Google"
                    "bing" -> "Bing"
                    "yahoo" -> "Yahoo"
                    "duckduckgo" -> "DuckDuckGo"
                    else -> engine.id.replaceFirstChar { it.uppercase() }
                }
                SearchEnginePillButton(
                    name = displayName,
                    onClick = { onEngineClicked(engine) },
                    isCursorMode = isCursorMode
                )
            }
        }
    }
}

@Composable
fun SearchEnginePillButton(
    name: String,
    onClick: () -> Unit,
    isCursorMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val showHighlight = isHovered || (isFocused && !isCursorMode)

    val scale by animateFloatAsState(
        targetValue = if (showHighlight) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "EngineScale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (showHighlight) 0.6f else 0.15f,
        label = "EngineBorderAlpha"
    )

    val containerColor by animateColorAsState(
        targetValue = if (showHighlight) {
            Color(0xFFFFB300).copy(alpha = 0.15f) // Glowing amber background
        } else {
            Color.White.copy(alpha = 0.04f)
        },
        label = "EngineBg"
    )

    val textColor by animateColorAsState(
        targetValue = if (showHighlight) {
            Color(0xFFFFD54F) // Gold text
        } else {
            Color(0xFFFFF8E1).copy(alpha = 0.7f)
        },
        label = "EngineText"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = Color(0xFFFFB300).copy(alpha = borderAlpha),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color(0xFFFFB300)),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}


internal fun handleKeyboardShortcut(
    event: KeyEvent,
    browserViewModel: BrowserViewModel,
    mainViewModel: MainViewModel
) {
    val isCtrl = event.isCtrlPressed
    val isAlt = event.isAltPressed

    when (event.keyCode) {
        KeyEvent.KEYCODE_T -> if (isCtrl) browserViewModel.createNewTab()
        KeyEvent.KEYCODE_W -> if (isCtrl) {
            val activeId = mainViewModel.tabManagementUseCase.activeTabId.value
            mainViewModel.tabManagementUseCase.closeTab(activeId)
        }
        KeyEvent.KEYCODE_L -> if (isCtrl) browserViewModel.showToolbar()
        KeyEvent.KEYCODE_D -> if (isCtrl) browserViewModel.toggleBookmark()
        KeyEvent.KEYCODE_DPAD_LEFT -> if (isAlt) browserViewModel.onBackPressed()
        KeyEvent.KEYCODE_DPAD_RIGHT -> if (isAlt) browserViewModel.onForwardPressed()
    }
}
