package za.kilowatch.hawkeyetvbrowser.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.kilowatch.hawkeyetvbrowser.domain.model.Bookmark
import za.kilowatch.hawkeyetvbrowser.domain.usecase.BookmarkUseCase
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkUseCase: BookmarkUseCase
) : ViewModel() {

    private val _currentFolderId = MutableStateFlow<String?>(null)
    val currentFolderId: StateFlow<String?> = _currentFolderId.asStateFlow()

    private val _breadcrumbs = MutableStateFlow<List<Pair<String?, String>>>(listOf(null to "Root"))
    val breadcrumbs: StateFlow<List<Pair<String?, String>>> = _breadcrumbs.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    init {
        loadFolder(null)
    }

    fun navigateToFolder(folderId: String, folderName: String) {
        val currentStack = _breadcrumbs.value.toMutableList()
        currentStack.add(folderId to folderName)
        _breadcrumbs.value = currentStack
        _currentFolderId.value = folderId
        loadFolder(folderId)
    }

    fun navigateUp() {
        val currentStack = _breadcrumbs.value.toMutableList()
        if (currentStack.size > 1) {
            currentStack.removeAt(currentStack.size - 1)
            _breadcrumbs.value = currentStack
            val previousFolderId = currentStack.last().first
            _currentFolderId.value = previousFolderId
            loadFolder(previousFolderId)
        }
    }

    private fun loadFolder(folderId: String?) {
        viewModelScope.launch {
            bookmarkUseCase.getBookmarksInFolder(folderId).collect { list ->
                _bookmarks.value = list
            }
        }
    }

    fun createFolder(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            bookmarkUseCase.createFolder(name, _currentFolderId.value)
        }
    }

    fun deleteBookmark(id: String) {
        viewModelScope.launch {
            bookmarkUseCase.removeBookmark(id)
        }
    }
}
