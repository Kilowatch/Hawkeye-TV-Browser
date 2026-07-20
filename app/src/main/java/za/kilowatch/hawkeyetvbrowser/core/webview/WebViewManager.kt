package za.kilowatch.hawkeyetvbrowser.core.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import dagger.hilt.android.qualifiers.ApplicationContext
import za.kilowatch.hawkeyetvbrowser.BuildConfig
import za.kilowatch.hawkeyetvbrowser.core.util.UserAgentBuilder
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

@Singleton
class WebViewManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepo: SettingsRepository
) {
    private val webViewPool = ArrayDeque<WebView>(3)
    private val incognitoWebViews = mutableListOf<WebView>()
    private val lock = ReentrantLock()

    @SuppressLint("SetJavaScriptEnabled")
    fun configureWebView(webView: WebView, incognito: Boolean = false) {
        webView.settings.apply {
            javaScriptEnabled = settingsRepo.isJavaScriptEnabled()
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            textZoom = settingsRepo.getTextZoom()

            mediaPlaybackRequiresUserGesture = true
            allowFileAccess = true
            allowContentAccess = true

            val mode = if (settingsRepo.isDesktopMode()) {
                UserAgentBuilder.Mode.DESKTOP
            } else {
                UserAgentBuilder.Mode.MOBILE
            }
            userAgentString = UserAgentBuilder.build(mode)

            setSupportMultipleWindows(settingsRepo.isPopupBlockingEnabled())
            javaScriptCanOpenWindowsAutomatically = !settingsRepo.isPopupBlockingEnabled()
        }

        // Configure Cookie Manager
        val cookieManager = CookieManager.getInstance()
        when (settingsRepo.getCookiePolicy()) {
            "BLOCK_ALL" -> {
                cookieManager.setAcceptCookie(false)
                cookieManager.setAcceptThirdPartyCookies(webView, false)
            }
            "BLOCK_THIRD_PARTY" -> {
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(webView, false)
            }
            "ALLOW_ALL" -> {
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(webView, true)
            }
        }

        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

        // Enable scrollbars and keep them permanently visible for mouse interaction and scroll status
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.isScrollbarFadingEnabled = false
        webView.scrollBarStyle = android.view.View.SCROLLBARS_INSIDE_OVERLAY

        // Only enable remote debugging in debug builds
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        if (incognito) {
            setupIncognitoMode(webView)
        }
    }

    fun acquireWebView(incognito: Boolean = false): WebView {
        lock.withLock {
            if (incognito) {
                val wv = WebView(context)
                configureWebView(wv, incognito = true)
                incognitoWebViews.add(wv)
                return wv
            }
            return webViewPool.removeFirstOrNull() ?: WebView(context).also {
                configureWebView(it)
            }
        }
    }

    fun releaseWebView(webView: WebView) {
        lock.withLock {
            webView.stopLoading()
            webView.loadUrl("about:blank")

            if (incognitoWebViews.contains(webView)) {
                cleanupIncognitoWebView(webView)
                incognitoWebViews.remove(webView)
                destroySafely(webView)
                return
            }

            if (webViewPool.size < 3) {
                webViewPool.addLast(webView)
            } else {
                destroySafely(webView)
            }
        }
    }

    fun destroyAll() {
        lock.withLock {
            webViewPool.forEach { destroySafely(it) }
            webViewPool.clear()
            incognitoWebViews.forEach { webView ->
                cleanupIncognitoWebView(webView)
                destroySafely(webView)
            }
            incognitoWebViews.clear()
        }
    }

    private fun destroySafely(webView: WebView) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (webView.isAttachedToWindow) {
                    webView.destroy()
                }
            } else {
                webView.destroy()
            }
        } catch (_: Exception) { }
    }

    private fun setupIncognitoMode(webView: WebView) {
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    private fun cleanupIncognitoWebView(webView: WebView) {
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        CookieManager.getInstance().removeSessionCookies(null)
        try {
            val cacheDir = File(context.cacheDir, "incognito_cache")
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
            }
        } catch (_: Exception) { }
    }
}
