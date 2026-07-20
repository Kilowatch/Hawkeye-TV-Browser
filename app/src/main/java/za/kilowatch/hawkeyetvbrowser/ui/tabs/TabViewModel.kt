package za.kilowatch.hawkeyetvbrowser.ui.tabs

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import za.kilowatch.hawkeyetvbrowser.domain.model.Tab
import za.kilowatch.hawkeyetvbrowser.domain.usecase.TabManagementUseCase
import javax.inject.Inject

@HiltViewModel
class TabViewModel @Inject constructor(
    private val tabManagementUseCase: TabManagementUseCase
) : ViewModel() {

    val tabs: StateFlow<List<Tab>> = tabManagementUseCase.tabs
    val activeTabId: StateFlow<String> = tabManagementUseCase.activeTabId

    fun selectTab(tabId: String) {
        tabManagementUseCase.switchToTab(tabId)
    }

    fun closeTab(tabId: String) {
        tabManagementUseCase.closeTab(tabId)
    }

    fun createNewTab() {
        tabManagementUseCase.createTab()
    }

    fun createIncognitoTab() {
        tabManagementUseCase.createTab(incognito = true)
    }
}
