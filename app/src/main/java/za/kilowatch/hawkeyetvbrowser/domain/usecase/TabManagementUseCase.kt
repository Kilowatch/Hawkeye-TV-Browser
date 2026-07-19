package za.kilowatch.hawkeyetvbrowser.domain.usecase

import android.webkit.WebView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import za.kilowatch.hawkeyetvbrowser.core.webview.WebViewManager
import za.kilowatch.hawkeyetvbrowser.domain.model.Tab
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabManagementUseCase @Inject constructor(
    private val webViewManager: WebViewManager
) {
    private val _tabs = MutableStateFlow<List<Tab>>(listOf(createDefaultTab()))
    val tabs: StateFlow<List<Tab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String>(_tabs.value.first().id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val webViewMap = mutableMapOf<String, WebView>()

    private fun createDefaultTab(): Tab = Tab(
        title = "New Tab",
        url = "about:blank"
    )

    fun createTab(url: String = "about:blank", incognito: Boolean = false): Tab {
        val tab = Tab(
            url = url,
            title = if (incognito) "Incognito Tab" else "New Tab",
            isIncognito = incognito
        )

        // Defer WebView creation — only create when tab is first viewed (in getOrCreateWebView)
        _tabs.value = _tabs.value + tab
        _activeTabId.value = tab.id
        return tab
    }

    fun closeTab(tabId: String) {
        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) {
            createTab()
            closeTabInternal(tabId)
            return
        }

        val closedIndex = currentTabs.indexOfFirst { it.id == tabId }
        closeTabInternal(tabId)

        if (_activeTabId.value == tabId || _tabs.value.none { it.id == _activeTabId.value }) {
            val newIndex = closedIndex.coerceAtMost(_tabs.value.size - 1)
            if (_tabs.value.isNotEmpty()) {
                _activeTabId.value = _tabs.value[newIndex].id
            }
        }
    }

    private fun closeTabInternal(tabId: String) {
        webViewMap.remove(tabId)?.let { webView ->
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webViewManager.releaseWebView(webView)
        }
        _tabs.value = _tabs.value.filter { it.id != tabId }
    }

    fun switchToTab(tabId: String) {
        if (_tabs.value.any { it.id == tabId }) {
            _activeTabId.value = tabId
        }
    }

    fun closeAllTabs() {
        webViewMap.forEach { (_, webView) ->
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webViewManager.releaseWebView(webView)
        }
        webViewMap.clear()
        _tabs.value = listOf(createDefaultTab())
        _activeTabId.value = _tabs.value.first().id
    }

    fun getWebViewForTab(tabId: String): WebView? = webViewMap[tabId]

    fun getOrCreateWebView(tabId: String): WebView? {
        if (webViewMap.containsKey(tabId)) return webViewMap[tabId]

        val tab = _tabs.value.find { it.id == tabId } ?: return null
        val wv = webViewManager.acquireWebView(tab.isIncognito)
        webViewMap[tabId] = wv

        if (tab.url != "about:blank" && tab.url.isNotBlank()) {
            wv.loadUrl(tab.url)
        }
        return wv
    }

    fun updateTab(tabId: String, update: (Tab) -> Tab) {
        _tabs.value = _tabs.value.map { if (it.id == tabId) update(it) else it }
    }

    fun onLowMemory() {
        val activeId = _activeTabId.value
        // Create a snapshot of keys to avoid ConcurrentModificationException
        webViewMap.keys.toList()
            .filter { it != activeId }
            .forEach { id ->
                webViewMap[id]?.let { webView ->
                    webView.stopLoading()
                    webViewManager.releaseWebView(webView)
                    webViewMap.remove(id)
                }
            }
    }
}
