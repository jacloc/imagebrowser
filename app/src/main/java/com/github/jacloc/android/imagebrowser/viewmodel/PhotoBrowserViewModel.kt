package com.github.jacloc.android.imagebrowser.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val isLoading: Boolean = true,
    val photoCollection: PhotoCollection? = null,
    val errorMessage: String? = null,
    val searchText: MutableState<String> = mutableStateOf("")
)

@HiltViewModel
class PhotoBrowserViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        loadRecentPhotos()
    }

    private fun loadRecentPhotos() {
        setLoading()
        viewModelScope.launch {
            updatePhotoCollectionResult(photoRepository.getRecentPhotos())
        }
    }

    fun search(searchText: String) {
        if (searchText.isEmpty()) {
            loadRecentPhotos()
            return
        }

        setLoading()
        viewModelScope.launch {
            updatePhotoCollectionResult(
                photoRepository.searchPhotos(searchText)
            )
        }
    }

    private fun updatePhotoCollectionResult(photoCollectionResult: Result<PhotoCollection>) {
        _uiStateFlow.update { prev ->
            UiState(
                isLoading = false,
                photoCollection = photoCollectionResult.getOrNull() ?: prev.photoCollection,
                errorMessage = photoCollectionResult.exceptionOrNull()?.toString(),
                searchText = prev.searchText
            )
        }
    }

    private fun setLoading() {
        _uiStateFlow.update { prev ->
            prev.copy(isLoading = true, errorMessage = null)
        }
    }
}
