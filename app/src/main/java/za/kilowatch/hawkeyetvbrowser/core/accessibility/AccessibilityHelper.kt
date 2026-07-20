package za.kilowatch.hawkeyetvbrowser.core.accessibility

import android.webkit.WebView
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityHelper @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private val highContrastCss = """
        (function() {
            var style = document.getElementById('hawkeye-high-contrast');
            if (!style) {
                style = document.createElement('style');
                style.id = 'hawkeye-high-contrast';
                style.innerHTML = `
                    * {
                        background-color: #000000 !important;
                        color: #00E5FF !important;
                        border-color: #333333 !important;
                    }
                    a { color: #FFFF00 !important; text-decoration: underline !important; }
                    img, video { opacity: 0.85 !important; filter: contrast(120%); }
                `;
                document.head.appendChild(style);
            }
        })();
    """.trimIndent()

    private val forceZoomJs = """
        (function() {
            var meta = document.querySelector('meta[name="viewport"]');
            if (meta) {
                meta.setAttribute('content', 'width=device-width, initial-scale=1.0, user-scalable=yes, maximum-scale=5.0');
            }
        })();
    """.trimIndent()

    fun applyPageAccessibility(webView: WebView) {
        if (settingsRepository.isHighContrastEnabled()) {
            webView.evaluateJavascript(highContrastCss, null)
        }

        if (settingsRepository.isForceZoomEnabled()) {
            webView.settings.setSupportZoom(true)
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.evaluateJavascript(forceZoomJs, null)
        }
    }
}
