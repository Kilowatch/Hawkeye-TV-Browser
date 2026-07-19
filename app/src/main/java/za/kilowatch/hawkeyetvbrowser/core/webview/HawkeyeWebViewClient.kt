package za.kilowatch.hawkeyetvbrowser.core.webview

import android.graphics.Bitmap
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError

class HawkeyeWebViewClient(
    private val onPageStarted: (url: String) -> Unit = {},
    private val onPageFinished: (url: String, title: String?) -> Unit = { _, _ -> },
    private val onProgressChanged: (progress: Int) -> Unit = {},
    private val onUrlChanged: (url: String) -> Unit = {},
    private val onSslError: (handler: SslErrorHandler, error: SslError) -> Unit = { handler, _ ->
        handler.cancel()
    }
) : WebViewClient() {

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
