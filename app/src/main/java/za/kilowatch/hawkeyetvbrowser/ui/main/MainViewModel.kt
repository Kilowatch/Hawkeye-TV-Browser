package za.kilowatch.hawkeyetvbrowser.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.kilowatch.hawkeyetvbrowser.domain.model.Tab
import za.kilowatch.hawkeyetvbrowser.domain.usecase.TabManagementUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val tabManagementUseCase: TabManagementUseCase
) : ViewModel() {

    val tabs: StateFlow<List<Tab>> = tabManagementUseCase.tabs
    val activeTabId: StateFlow<String> = tabManagementUseCase.activeTabId

    private val _selectedTabId = MutableStateFlow<String>("")
    val selectedTabId: StateFlow<String> = _selectedTabId.asStateFlow()

    fun selectTabForNavigation(tabId: String) {
        _selectedTabId.value = tabId
    }

    override fun onCleared() {
        super.onCleared()
        tabManagementUseCase.closeAllTabs()
    }
}
