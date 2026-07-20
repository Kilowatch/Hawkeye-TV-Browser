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

    private val _dohProvider = MutableStateFlow(settingsRepo.getDohProvider())
    val dohProvider: StateFlow<String> = _dohProvider.asStateFlow()

    private val _isAdBlockEnabled = MutableStateFlow(settingsRepo.isAdBlockEnabled())
    val isAdBlockEnabled: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()

    private val _isSafeBrowsingEnabled = MutableStateFlow(settingsRepo.isSafeBrowsingEnabled())
    val isSafeBrowsingEnabled: StateFlow<Boolean> = _isSafeBrowsingEnabled.asStateFlow()

    private val _isHighContrastEnabled = MutableStateFlow(settingsRepo.isHighContrastEnabled())
    val isHighContrastEnabled: StateFlow<Boolean> = _isHighContrastEnabled.asStateFlow()

    private val _isForceZoomEnabled = MutableStateFlow(settingsRepo.isForceZoomEnabled())
    val isForceZoomEnabled: StateFlow<Boolean> = _isForceZoomEnabled.asStateFlow()

    private val _cookiePolicy = MutableStateFlow(settingsRepo.getCookiePolicy())
    val cookiePolicy: StateFlow<String> = _cookiePolicy.asStateFlow()

    private val _isJavaScriptEnabled = MutableStateFlow(settingsRepo.isJavaScriptEnabled())
    val isJavaScriptEnabled: StateFlow<Boolean> = _isJavaScriptEnabled.asStateFlow()

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

    fun setDohProvider(provider: String) {
        _dohProvider.value = provider
        settingsRepo.setDohProvider(provider)
    }

    fun toggleAdBlock() {
        val newValue = !_isAdBlockEnabled.value
        _isAdBlockEnabled.value = newValue
        settingsRepo.setAdBlockEnabled(newValue)
    }

    fun toggleSafeBrowsing() {
        val newValue = !_isSafeBrowsingEnabled.value
        _isSafeBrowsingEnabled.value = newValue
        settingsRepo.setSafeBrowsingEnabled(newValue)
    }

    fun toggleHighContrast() {
        val newValue = !_isHighContrastEnabled.value
        _isHighContrastEnabled.value = newValue
        settingsRepo.setHighContrastEnabled(newValue)
    }

    fun toggleForceZoom() {
        val newValue = !_isForceZoomEnabled.value
        _isForceZoomEnabled.value = newValue
        settingsRepo.setForceZoomEnabled(newValue)
    }

    fun setCookiePolicy(policy: String) {
        _cookiePolicy.value = policy
        settingsRepo.setCookiePolicy(policy)
    }

    fun toggleJavaScript() {
        val newValue = !_isJavaScriptEnabled.value
        _isJavaScriptEnabled.value = newValue
        settingsRepo.setJavaScriptEnabled(newValue)
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
