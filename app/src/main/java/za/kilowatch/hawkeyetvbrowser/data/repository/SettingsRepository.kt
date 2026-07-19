package za.kilowatch.hawkeyetvbrowser.data.repository

import android.content.SharedPreferences
import za.kilowatch.hawkeyetvbrowser.domain.model.SearchEngine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val prefs: SharedPreferences
) {
    companion object {
        private const val KEY_SEARCH_ENGINE = "search_engine"
        private const val KEY_TEXT_ZOOM = "text_zoom"
        private const val KEY_DESKTOP_MODE = "desktop_mode"
        private const val KEY_DNT_ENABLED = "dnt_enabled"
        private const val KEY_POPUP_BLOCKING = "popup_blocking"
        private const val KEY_CURSOR_COLOR = "cursor_color"
        private const val KEY_CURSOR_SPEED = "cursor_speed"
        private const val KEY_NAV_MODE = "navigation_mode"

        private const val DEFAULT_TEXT_ZOOM = 100
        private const val DEFAULT_CURSOR_COLOR = -0x1a1b00 // neon cyan
        private const val DEFAULT_CURSOR_SPEED = 10f
    }

    // Search Engine
    fun getSearchEngine(): SearchEngine {
        val id = prefs.getString(KEY_SEARCH_ENGINE, "google") ?: "google"
        return SearchEngine.fromId(id)
    }

    fun setSearchEngine(id: String) {
        prefs.edit().putString(KEY_SEARCH_ENGINE, id).apply()
    }

    // Text Zoom (50-200)
    fun getTextZoom(): Int = prefs.getInt(KEY_TEXT_ZOOM, DEFAULT_TEXT_ZOOM)
        .coerceIn(50, 200)

    fun setTextZoom(zoom: Int) {
        prefs.edit().putInt(KEY_TEXT_ZOOM, zoom.coerceIn(50, 200)).apply()
    }

    // Desktop Mode
    fun isDesktopMode(): Boolean = prefs.getBoolean(KEY_DESKTOP_MODE, false)

    fun setDesktopMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DESKTOP_MODE, enabled).apply()
    }

    // Do Not Track
    fun isDntEnabled(): Boolean = prefs.getBoolean(KEY_DNT_ENABLED, false)

    fun setDntEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DNT_ENABLED, enabled).apply()
    }

    // Pop-up Blocking
    fun isPopupBlockingEnabled(): Boolean = prefs.getBoolean(KEY_POPUP_BLOCKING, true)

    fun setPopupBlocking(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_POPUP_BLOCKING, enabled).apply()
    }

    // Cursor Color
    fun getCursorColor(): Int = prefs.getInt(KEY_CURSOR_COLOR, DEFAULT_CURSOR_COLOR)

    fun setCursorColor(color: Int) {
        prefs.edit().putInt(KEY_CURSOR_COLOR, color).apply()
    }

    // Cursor Speed
    fun getCursorSpeed(): Float = prefs.getFloat(KEY_CURSOR_SPEED, DEFAULT_CURSOR_SPEED)

    fun setCursorSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_CURSOR_SPEED, speed).apply()
    }

    // Navigation Mode (true = CURSOR, false = SPATIAL)
    fun isCursorMode(): Boolean = prefs.getBoolean(KEY_NAV_MODE, true)

    fun setCursorMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NAV_MODE, enabled).apply()
    }
}
