package za.kilowatch.hawkeyetvbrowser.core.input

import android.view.KeyEvent
import android.webkit.WebView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CursorController @Inject constructor() {

    private var _cursorX = 960f
    private var _cursorY = 540f
    private var _viewWidth = 1920f
    private var _viewHeight = 1080f

    var cursorSpeed = 10f
        set(value) { field = value.coerceIn(1f, 50f) }
    var cursorAcceleration = 1.5f
        set(value) { field = value.coerceAtLeast(1f) }

    /**
     * Height of the scroll trigger zone at the top and bottom edges (pixels).
     * When the cursor enters this region and the user keeps pressing D-pad
     * up/down, the page scrolls rather than the cursor moving further.
     */
    var scrollZoneHeight = 120f

    /**
     * Base number of WebView pixels scrolled per key event when in a scroll zone.
     * Acceleration is applied on repeated key presses.
     */
    var scrollAmount = 60f

    var onCursorMoved: (x: Float, y: Float) -> Unit = { _, _ -> }
    var onClickAt: (x: Float, y: Float) -> Unit = { _, _ -> }

    /**
     * Called when an edge-scroll should occur. `dy` is positive for scrolling
     * down (content moves up) and negative for scrolling up.
     */
    var onScrollBy: (dy: Float) -> Unit = { _ -> }

    /**
     * When false the top scroll zone is inactive (e.g. while the toolbar is
     * visible) so the cursor can still reach toolbar buttons without
     * accidentally triggering page-up scrolling.
     */
    var topScrollZoneEnabled: Boolean = false

    /** Dynamic toolbar parameters passed from Compose */
    var toolbarHeight: Float = 0f
    var toolbarVisible: Boolean = true

    val cursorX: Float get() = _cursorX
    val cursorY: Float get() = _cursorY

    fun handleKey(event: KeyEvent, webView: WebView): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        val accel = if (event.repeatCount > 0) cursorAcceleration else 1f
        val speed = cursorSpeed * accel

        when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                val topLimit = if (toolbarVisible) toolbarHeight else 0f
                val canScrollUp = webView.canScrollVertically(-1)
                if (topScrollZoneEnabled && _cursorY <= topLimit + scrollZoneHeight && canScrollUp) {
                    if (toolbarVisible && _cursorY < toolbarHeight) {
                        // Cursor is inside the toolbar — let it move up to click buttons
                        moveCursor(0f, -speed)
                    } else {
                        // Cursor is just below the toolbar — scroll the page upward
                        onScrollBy(-scrollAmount * accel)
                    }
                } else {
                    moveCursor(0f, -speed)
                }
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                val canScrollDown = webView.canScrollVertically(1)
                if (_cursorY >= _viewHeight - scrollZoneHeight && canScrollDown) {
                    // Cursor is in the bottom scroll zone — scroll the page downward
                    onScrollBy(scrollAmount * accel)
                } else {
                    moveCursor(0f, speed)
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> moveCursor(-speed, 0f)
            KeyEvent.KEYCODE_DPAD_RIGHT -> moveCursor(speed, 0f)
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                onClickAt(_cursorX, _cursorY)
                return true
            }
            KeyEvent.KEYCODE_BACK -> return false
            else -> return false
        }

        onCursorMoved(_cursorX, _cursorY)
        return true
    }

    fun updateViewSize(width: Int, height: Int) {
        _viewWidth = width.toFloat().coerceAtLeast(1f)
        _viewHeight = height.toFloat().coerceAtLeast(1f)
    }

    private fun moveCursor(dx: Float, dy: Float) {
        _cursorX = (_cursorX + dx).coerceIn(0f, _viewWidth - 1f)
        _cursorY = (_cursorY + dy).coerceIn(0f, _viewHeight - 1f)
    }
}

