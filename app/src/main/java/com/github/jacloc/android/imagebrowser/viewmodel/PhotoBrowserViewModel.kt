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
    val searchText: MutableState<String> = mutableStateOf(""),
    val isLoadingMore: Boolean = false,
)

@HiltViewModel
class PhotoBrowserViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    // Flag indicating which data source to use when loading more (recent or search)
    private var useSearchDataForPaging = false

    init {
        loadRecentPhotos()
    }

    fun loadMorePhotos() {
        val existingPhotoCollection = _uiStateFlow.value.photoCollection ?: return
        val nextPageIndex = existingPhotoCollection.currentPage + 1
        if (nextPageIndex > existingPhotoCollection.totalPages) {
            return
        }

        viewModelScope.launch {
            _uiStateFlow.update {
                it.copy(isLoadingMore = true)
            }
            val nextPage = if (useSearchDataForPaging) {
                photoRepository.searchPhotos(
                    query = _uiStateFlow.value.searchText.value,
                    page = nextPageIndex
                )
            } else {
                photoRepository.getRecentPhotos(page = nextPageIndex)
            }

            updatePhotoCollectionResult(
                nextPage.map { existingPhotoCollection.appendNextPage(it) }
            )
        }
    }

    private fun PhotoCollection.appendNextPage(nextPage: PhotoCollection): PhotoCollection {
        return PhotoCollection(
            currentPage = nextPage.currentPage,
            totalPages = nextPage.totalPages,
            pageSize = nextPage.pageSize,
            total = nextPage.total,
            photoList = buildList {
                addAll(photoList)
                addAll(nextPage.photoList)
            }
        )
    }

    private fun loadRecentPhotos() {
        setLoading()
        viewModelScope.launch {
            val photoCollectionResult = photoRepository.getRecentPhotos()
            if (photoCollectionResult.isSuccess) {
                useSearchDataForPaging = false
            }
            updatePhotoCollectionResult(photoCollectionResult)
        }
    }

    fun search(searchText: String) {
        if (searchText.isEmpty()) {
            loadRecentPhotos()
            return
        }

        setLoading()
        viewModelScope.launch {
            val photoCollectionResult = photoRepository.searchPhotos(searchText)
            if (photoCollectionResult.isSuccess) {
                useSearchDataForPaging = true
            }
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
                searchText = prev.searchText,
                isLoadingMore = false
            )
        }
    }

    private fun setLoading() {
        _uiStateFlow.update { prev ->
            prev.copy(isLoading = true, errorMessage = null)
        }
    }
}
