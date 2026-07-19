package za.kilowatch.hawkeyetvbrowser.core.input

import android.view.KeyEvent
import android.webkit.WebView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phase 1 basic implementation. Routes D-pad events to the Android focus system.
 * Enhanced link-aware spatial navigation deferred to Phase 2.
 */
@Singleton
class SpatialNavigationHelper @Inject constructor() {

    fun handleKey(event: KeyEvent, webView: WebView): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        // Let the standard Android focus system handle navigation.
        // For Phase 1, simply route these events to the WebView.
        return when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER -> {
                webView.requestFocus()
                false // Return false to let the system handle it
            }
            else -> false
        }
    }
}
