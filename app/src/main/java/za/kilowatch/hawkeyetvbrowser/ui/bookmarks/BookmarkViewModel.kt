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

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    init {
        viewModelScope.launch {
            bookmarkUseCase.getAllBookmarks().collect { list ->
                _bookmarks.value = list
            }
        }
    }

    fun deleteBookmark(id: String) {
        viewModelScope.launch {
            bookmarkUseCase.removeBookmark(id)
        }
    }
}
