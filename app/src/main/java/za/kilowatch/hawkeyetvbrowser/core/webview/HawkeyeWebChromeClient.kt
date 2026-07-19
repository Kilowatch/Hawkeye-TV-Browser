package za.kilowatch.hawkeyetvbrowser.core.webview

import android.graphics.Bitmap
import android.view.View
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView

class HawkeyeWebChromeClient(
    private val onProgressChanged: (progress: Int) -> Unit = {},
    private val onReceivedTitle: (title: String) -> Unit = {},
    private val onReceivedIcon: (icon: Bitmap?) -> Unit = {},
    private val onShowFullScreen: (view: View, callback: WebChromeClient.CustomViewCallback) -> Unit = { _, _ -> },
    private val onHideFullScreen: () -> Unit = {}
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        onProgressChanged(newProgress)
    }

    override fun onReceivedTitle(view: WebView, title: String?) {
        title?.let { onReceivedTitle(it) }
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        onReceivedIcon(icon)
    }

    // ── Full-Screen Video Support ──
    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        onShowFullScreen(view, callback)
    }

    override fun onHideCustomView() {
        onHideFullScreen()
    }

    // ── JavaScript Dialogs ──
    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        // Return false to let the hosting Activity handle it via AlertDialog
        return false
    }

    override fun onJsConfirm(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        return false
    }

    override fun onJsPrompt(
        view: WebView,
        url: String,
        message: String,
        defaultValue: String?,
        result: JsPromptResult
    ): Boolean {
        return false
    }
}
