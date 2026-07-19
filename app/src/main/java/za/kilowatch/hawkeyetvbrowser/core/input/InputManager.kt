package za.kilowatch.hawkeyetvbrowser.core.input

import android.view.KeyEvent
import android.webkit.WebView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InputManager @Inject constructor(
    val cursorController: CursorController,
    private val spatialNavHelper: SpatialNavigationHelper
) {
    enum class NavigationMode { SPATIAL, CURSOR }

    var currentMode: NavigationMode = NavigationMode.CURSOR

    var onMenuPressed: () -> Unit = {}
    var onToggleMode: () -> Unit = {}

    fun handleKeyEvent(event: KeyEvent, webView: WebView): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        // Global shortcuts
        when (event.keyCode) {
            KeyEvent.KEYCODE_MENU -> {
                onMenuPressed()
                return true
            }
            KeyEvent.KEYCODE_UNKNOWN -> {
                // TV remote "menu" button fallback
                onMenuPressed()
                return true
            }
        }

        // Mode-specific handling
        return when (currentMode) {
            NavigationMode.SPATIAL -> spatialNavHelper.handleKey(event, webView)
            NavigationMode.CURSOR -> cursorController.handleKey(event, webView)
        }
    }

    fun toggleMode() {
        currentMode = if (currentMode == NavigationMode.SPATIAL) {
            NavigationMode.CURSOR
        } else {
            NavigationMode.SPATIAL
        }
        onToggleMode()
    }

    fun setCursorMode(enabled: Boolean) {
        currentMode = if (enabled) NavigationMode.CURSOR else NavigationMode.SPATIAL
    }
}
