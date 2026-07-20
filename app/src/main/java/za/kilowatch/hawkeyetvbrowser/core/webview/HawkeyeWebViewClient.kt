package za.kilowatch.hawkeyetvbrowser.core.webview

import android.graphics.Bitmap
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError

import android.webkit.SafeBrowsingResponse
import android.webkit.WebResourceResponse
import za.kilowatch.hawkeyetvbrowser.core.adblock.AdBlockEngine
import java.io.ByteArrayInputStream

class HawkeyeWebViewClient(
    private val adBlockEngine: AdBlockEngine? = null,
    private val onPageStarted: (url: String) -> Unit = {},
    private val onPageFinished: (url: String, title: String?) -> Unit = { _, _ -> },
    private val onProgressChanged: (progress: Int) -> Unit = {},
    private val onUrlChanged: (url: String) -> Unit = {},
    private val onSafeBrowsingHitDetected: (url: String, threatType: Int) -> Unit = { _, _ -> },
    private val onSslError: (handler: SslErrorHandler, error: SslError) -> Unit = { handler, _ ->
        handler.cancel()
    }
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()
        if (adBlockEngine?.shouldBlock(url, request.requestHeaders) == true) {
            // Block request by returning empty resource response
            return WebResourceResponse(
                "text/plain",
                "utf-8",
                ByteArrayInputStream(byteArrayOf())
            )
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest,
        threatType: Int,
        callback: SafeBrowsingResponse
    ) {
        val url = request.url.toString()
        onSafeBrowsingHitDetected(url, threatType)
        callback.showInterstitial(true)
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onPageStarted(url)
        onUrlChanged(url)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        val title = view.title
        onPageFinished(url, title)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean {
        // Let the WebView handle normal navigation
        return false
    }

    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        onSslError(handler, error)
    }
}
