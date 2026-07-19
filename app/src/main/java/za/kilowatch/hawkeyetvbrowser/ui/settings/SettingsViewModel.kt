package za.kilowatch.hawkeyetvbrowser.ui.settings

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.kilowatch.hawkeyetvbrowser.data.repository.SettingsRepository
import za.kilowatch.hawkeyetvbrowser.domain.model.SearchEngine
import za.kilowatch.hawkeyetvbrowser.domain.usecase.HistoryUseCase
import za.kilowatch.hawkeyetvbrowser.ui.common.ClearDataOptions
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val historyUseCase: HistoryUseCase
) : ViewModel() {

    private val _textZoom = MutableStateFlow(settingsRepo.getTextZoom())
    val textZoom: StateFlow<Int> = _textZoom.asStateFlow()

    private val _isDesktopMode = MutableStateFlow(settingsRepo.isDesktopMode())
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    private val _isDntEnabled = MutableStateFlow(settingsRepo.isDntEnabled())
    val isDntEnabled: StateFlow<Boolean> = _isDntEnabled.asStateFlow()

    private val _isPopupBlocking = MutableStateFlow(settingsRepo.isPopupBlockingEnabled())
    val isPopupBlocking: StateFlow<Boolean> = _isPopupBlocking.asStateFlow()

    private val _selectedEngine = MutableStateFlow(settingsRepo.getSearchEngine())
    val selectedEngine: StateFlow<SearchEngine> = _selectedEngine.asStateFlow()

    private val _cursorSpeed = MutableStateFlow(settingsRepo.getCursorSpeed())
    val cursorSpeed: StateFlow<Float> = _cursorSpeed.asStateFlow()

    private var activeWebView: WebView? = null

    val searchEngines = SearchEngine.DEFAULTS

    fun setActiveWebView(webView: WebView?) {
        activeWebView = webView
    }

    fun setTextZoom(zoom: Int) {
        _textZoom.value = zoom
        settingsRepo.setTextZoom(zoom)
    }

    fun toggleDesktopMode() {
        val newValue = !_isDesktopMode.value
        _isDesktopMode.value = newValue
        settingsRepo.setDesktopMode(newValue)
    }

    fun toggleDnt() {
        val newValue = !_isDntEnabled.value
        _isDntEnabled.value = newValue
        settingsRepo.setDntEnabled(newValue)
    }

    fun togglePopupBlocking() {
        val newValue = !_isPopupBlocking.value
        _isPopupBlocking.value = newValue
        settingsRepo.setPopupBlocking(newValue)
    }

    fun setSearchEngine(engine: SearchEngine) {
        _selectedEngine.value = engine
        settingsRepo.setSearchEngine(engine.id)
    }

    fun setCursorSpeed(speed: Float) {
        _cursorSpeed.value = speed
        settingsRepo.setCursorSpeed(speed)
    }

    fun clearBrowsingData(options: ClearDataOptions) {
        viewModelScope.launch {
            if (options.clearHistory) {
                historyUseCase.clearAll()
            }
            if (options.clearCache) {
                activeWebView?.clearCache(true)
            }
            if (options.clearCookies) {
                android.webkit.CookieManager.getInstance().removeAllCookies(null)
            }
        }
    }
}
