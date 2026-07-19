package za.kilowatch.hawkeyetvbrowser.ui.browser

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import za.kilowatch.hawkeyetvbrowser.core.downloader.DestinationType
import za.kilowatch.hawkeyetvbrowser.core.downloader.DownloaderCodeValidator
import za.kilowatch.hawkeyetvbrowser.core.input.InputManager
import za.kilowatch.hawkeyetvbrowser.core.webview.HawkeyeWebChromeClient
import za.kilowatch.hawkeyetvbrowser.core.webview.HawkeyeWebViewClient
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import za.kilowatch.hawkeyetvbrowser.domain.model.Tab
import za.kilowatch.hawkeyetvbrowser.domain.usecase.BookmarkUseCase
import za.kilowatch.hawkeyetvbrowser.domain.usecase.HistoryUseCase
import za.kilowatch.hawkeyetvbrowser.domain.usecase.InputResolution
import za.kilowatch.hawkeyetvbrowser.domain.usecase.SearchUseCase
import za.kilowatch.hawkeyetvbrowser.domain.usecase.TabManagementUseCase
import javax.inject.Inject

data class DownloaderCodeInfo(
    val code: String,
    val resolvedUrl: String,
    val destinationHost: String,
    val destinationType: DestinationType,
    val isTrusted: Boolean
)

data class BrowserUiState(
    val currentUrl: String = "",
    val pageTitle: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isIncognito: Boolean = false,
    val isDesktopMode: Boolean = false,
    val isCursorMode: Boolean = false,
    val toolbarVisible: Boolean = true,
    val searchText: String = "",
    val isBookmarked: Boolean = false,
    val cursorX: Float = 960f,
    val cursorY: Float = 540f,
    val webPageBackgroundColor: Color? = null
)

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val tabManagementUseCase: TabManagementUseCase,
    private val searchUseCase: SearchUseCase,
    private val bookmarkUseCase: BookmarkUseCase,
    private val historyUseCase: HistoryUseCase,
    val inputManager: InputManager,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BrowserUiState())
    val state: StateFlow<BrowserUiState> = _state.asStateFlow()

    private val _pendingDownloaderCode = MutableStateFlow<DownloaderCodeInfo?>(null)
    val pendingDownloaderCode: StateFlow<DownloaderCodeInfo?> =
        _pendingDownloaderCode.asStateFlow()

    private val downloaderCodeValidator = DownloaderCodeValidator()

    private val _errorMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    val activeTab: StateFlow<Tab> = combine(
        tabManagementUseCase.tabs,
        tabManagementUseCase.activeTabId
    ) { tabs, activeId ->
        tabs.firstOrNull { it.id == activeId } ?: tabs.firstOrNull() ?: Tab()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = tabManagementUseCase.tabs.value.firstOrNull() ?: Tab()
    )

    // One-shot WebView client instances — recreated per WebView
    private fun createWebViewClient(): HawkeyeWebViewClient {
        return HawkeyeWebViewClient(
            onPageStarted = { url ->
                // Update searchText so the URL bar immediately shows the
                // destination URL (e.g. when clicking a link on Google)
                updateState {
                    copy(
                        currentUrl = url,
                        isLoading = true,
                        searchText = url,
                        webPageBackgroundColor = null
                    )
                }
                checkBookmarkStatus(url)
            },
            onPageFinished = { url, title ->
                // Use the final URL (after any redirects) for the URL bar
                updateState {
                    copy(
                        isLoading = false,
                        pageTitle = title ?: "",
                        currentUrl = url,
                        searchText = url
                    )
                }
                updateNavState()
                extractWebPageColor()

                if (!_state.value.isIncognito) {
                    viewModelScope.launch {
                        runCatching { historyUseCase.recordVisit(url, title) }
                    }
                }
            },
            onUrlChanged = { url ->
                updateState {
                    copy(
                        currentUrl = url,
                        searchText = url,
                        webPageBackgroundColor = null
                    )
                }
            }
        )
    }

    private fun createChromeClient(): HawkeyeWebChromeClient {
        return HawkeyeWebChromeClient(
            onProgressChanged = { progress ->
                updateState { copy(progress = progress, isLoading = progress < 100) }
            },
            onReceivedTitle = { title ->
                updateState { copy(pageTitle = title) }
            }
        )
    }

    init {
        val isCursor = settingsRepo.isCursorMode()
        updateState { copy(isCursorMode = isCursor) }
        inputManager.setCursorMode(isCursor)
        inputManager.onMenuPressed = { toggleToolbar() }

        // Top scroll zone is always enabled (its threshold shifts below the
        // toolbar dynamically when the toolbar is visible).
        inputManager.cursorController.topScrollZoneEnabled = true

        // Wire cursor position from CursorController to UI state
        inputManager.cursorController.onCursorMoved = { x, y ->
            updateState { copy(cursorX = x, cursorY = y) }
        }

        // Wire edge-scroll: when cursor is in the top/bottom scroll zone,
        // scroll the active WebView up or down instead of moving the cursor.
        inputManager.cursorController.onScrollBy = { dy ->
            getActiveWebView()?.post {
                getActiveWebView()?.scrollBy(0, dy.toInt())
            }
        }

        // onClickAt is wired in BrowserScreen (Compose layer) so it dispatches
        // through the root DecorView, hitting both Compose UI elements (toolbar
        // buttons, URL field) and the WebView at the cursor position.
    }

    fun setupWebViewClients(webView: WebView) {
        webView.webViewClient = createWebViewClient()
        webView.webChromeClient = createChromeClient()
    }

    /**
     * Routes D-pad key events through InputManager.
     * Returns true if the event was consumed.
     */
    fun handleDpadEvent(keyCode: Int, webView: WebView?): Boolean {
        val wv = webView ?: getActiveWebView() ?: return false
        val mode = inputManager.currentMode

        if (mode == za.kilowatch.hawkeyetvbrowser.core.input.InputManager.NavigationMode.CURSOR) {
            return when (keyCode) {
                android.view.KeyEvent.KEYCODE_DPAD_UP,
                android.view.KeyEvent.KEYCODE_DPAD_DOWN,
                android.view.KeyEvent.KEYCODE_DPAD_LEFT,
                android.view.KeyEvent.KEYCODE_DPAD_RIGHT,
                android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                android.view.KeyEvent.KEYCODE_ENTER -> {
                    val event = android.view.KeyEvent(
                        android.view.KeyEvent.ACTION_DOWN, keyCode
                    )
                    inputManager.cursorController.handleKey(event, wv)
                }
                else -> false
            }
        }
        return false
    }

    fun onUrlEntered(input: String) {
        viewModelScope.launch {
            when (val resolution = searchUseCase.resolveInput(input)) {
                is InputResolution.DirectUrl -> loadUrl(resolution.url)
                is InputResolution.SearchQuery -> loadUrl(resolution.url)
                is InputResolution.DownloaderCode -> {
                    val type = downloaderCodeValidator.classifyDestination(resolution.resolvedUrl)
                    val trusted = downloaderCodeValidator.isTrustedSource(resolution.resolvedUrl)
                    _pendingDownloaderCode.value = DownloaderCodeInfo(
                        code = resolution.code,
                        resolvedUrl = resolution.resolvedUrl,
                        destinationHost = resolution.destinationHost,
                        destinationType = type,
                        isTrusted = trusted
                    )
                }
                is InputResolution.DownloaderCodeError -> {
                    _errorMessage.tryEmit(resolution.reason)
                }
            }
        }
    }

    fun onDownloaderCodeConfirmed(url: String) {
        _pendingDownloaderCode.value = null
        loadUrl(url)
    }

    fun onDownloaderCodeDismissed() {
        _pendingDownloaderCode.value = null
    }

    fun loadUrl(url: String) {
        val activeId = tabManagementUseCase.activeTabId.value
        val wv = tabManagementUseCase.getOrCreateWebView(activeId)
        wv?.let {
            it.webViewClient = createWebViewClient()
            it.webChromeClient = createChromeClient()
            // Inject DNT header into main page load if enabled
            if (settingsRepo.isDntEnabled()) {
                it.loadUrl(url, mapOf("DNT" to "1"))
            } else {
                it.loadUrl(url)
            }
            updateState { copy(currentUrl = url, searchText = url) }
        }
    }

    fun onBackPressed() {
        getActiveWebView()?.let {
            if (it.canGoBack()) {
                it.goBack()
                updateNavState()
            }
        }
    }

    fun onForwardPressed() {
        getActiveWebView()?.let {
            if (it.canGoForward()) {
                it.goForward()
                updateNavState()
            }
        }
    }

    fun onRefresh() {
        getActiveWebView()?.reload()
    }

    fun onStopLoading() {
        getActiveWebView()?.stopLoading()
        updateState { copy(isLoading = false) }
    }

    fun toggleIncognito() {
        updateState { copy(isIncognito = !isIncognito) }
        tabManagementUseCase.createTab(incognito = !_state.value.isIncognito)
    }

    fun toggleDesktopMode() {
        val newState = !_state.value.isDesktopMode
        settingsRepo.setDesktopMode(newState)
        updateState { copy(isDesktopMode = newState) }
        getActiveWebView()?.reload()
    }

    fun toggleCursorMode() {
        val newState = !_state.value.isCursorMode
        settingsRepo.setCursorMode(newState)
        inputManager.setCursorMode(newState)
        updateState { copy(isCursorMode = newState) }
    }

    fun toggleToolbar() {
        val nowVisible = !_state.value.toolbarVisible
        updateState { copy(toolbarVisible = nowVisible) }
    }

    fun hideToolbar() {
        updateState { copy(toolbarVisible = false) }
    }

    fun showToolbar() {
        updateState { copy(toolbarVisible = true) }
    }

    fun updateSearchText(text: String) {
        updateState { copy(searchText = text) }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val url = _state.value.currentUrl
            if (url.isBlank()) return@launch

            runCatching {
                if (bookmarkUseCase.isBookmarked(url)) {
                    // Already bookmarked — navigate to bookmarks screen
                } else {
                    bookmarkUseCase.addBookmark(
                        title = _state.value.pageTitle.ifBlank { url },
                        url = url
                    )
                    updateState { copy(isBookmarked = true) }
                }
            }
        }
    }

    fun createNewTab() {
        tabManagementUseCase.createTab()
    }

    fun createIncognitoTab() {
        updateState { copy(isIncognito = true) }
        tabManagementUseCase.createTab(incognito = true)
    }

    private fun updateNavState() {
        getActiveWebView()?.let {
            updateState {
                copy(
                    canGoBack = it.canGoBack(),
                    canGoForward = it.canGoForward()
                )
            }
        }
    }

    private fun checkBookmarkStatus(url: String) {
        viewModelScope.launch {
            try {
                val bookmarked = bookmarkUseCase.isBookmarked(url)
                updateState { copy(isBookmarked = bookmarked) }
            } catch (_: Exception) {
                // Silently ignore — bookmark status is non-critical
            }
        }
    }

    fun zoomIn() {
        getActiveWebView()?.let { webView ->
            webView.post {
                webView.zoomIn()
            }
        }
    }

    fun zoomOut() {
        getActiveWebView()?.let { webView ->
            webView.post {
                webView.zoomOut()
            }
        }
    }

    private fun getActiveWebView(): WebView? {
        val activeId = tabManagementUseCase.activeTabId.value
        return tabManagementUseCase.getWebViewForTab(activeId)
    }

    private fun updateState(transform: BrowserUiState.() -> BrowserUiState) {
        _state.value = _state.value.transform()
    }

    fun extractWebPageColor() {
        val webView = getActiveWebView() ?: return
        webView.post {
            webView.evaluateJavascript(
                "(function() { " +
                "  var bg = window.getComputedStyle(document.body).backgroundColor; " +
                "  if (bg === 'rgba(0, 0, 0, 0)' || bg === 'transparent') { " +
                "    var htmlBg = window.getComputedStyle(document.documentElement).backgroundColor; " +
                "    if (htmlBg !== 'rgba(0, 0, 0, 0)' && htmlBg !== 'transparent') return htmlBg; " +
                "  } " +
                "  return bg; " +
                "})();"
            ) { value ->
                if (value != null && value != "null" && value != "\"rgba(0, 0, 0, 0)\"") {
                    val color = parseRgbColor(value)
                    if (color != null) {
                        updateState { copy(webPageBackgroundColor = color) }
                    } else {
                        updateState { copy(webPageBackgroundColor = null) }
                    }
                } else {
                    updateState { copy(webPageBackgroundColor = null) }
                }
            }
        }
    }

    private fun parseRgbColor(rgbStr: String): Color? {
        val clean = rgbStr.trim('"', ' ')
        if (clean.startsWith("rgb")) {
            val matches = Regex("""\d+""").findAll(clean).map { it.value.toInt() }.toList()
            if (matches.size >= 3) {
                return Color(matches[0], matches[1], matches[2])
            }
        } else if (clean.startsWith("#")) {
            runCatching {
                return Color(android.graphics.Color.parseColor(clean))
            }
        }
        return null
    }

    override fun onCleared() {
        inputManager.onMenuPressed = {}
        super.onCleared()
    }
}
