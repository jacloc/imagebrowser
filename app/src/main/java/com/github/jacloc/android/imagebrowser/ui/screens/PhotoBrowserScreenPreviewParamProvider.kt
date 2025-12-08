package com.github.jacloc.android.imagebrowser.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.jacloc.android.imagebrowser.data.domain.Photo
import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.viewmodel.UiState

class PhotoBrowserScreenPreviewParamProvider : PreviewParameterProvider<UiState> {
    override val values: Sequence<UiState>
        get() = sequenceOf(
            PhotoBrowserScreenParams.LOADING_STATE,
            PhotoBrowserScreenParams.ERROR_STATE,
            PhotoBrowserScreenParams.SUCCESS_STATE,
            PhotoBrowserScreenParams.SEARCH_STATE
        )
}

object PhotoBrowserScreenParams {
    val LOADING_STATE = UiState()

    val ERROR_STATE = UiState(
        isLoading = false,
        errorMessage = "Some error occurred!"
    )

    val SUCCESS_STATE = UiState(
        isLoading = false,
        photoCollection =
            PhotoCollection(
                currentPage = 1,
                totalPages = 10,
                pageSize = 100,
                photoList = listOf(
                    Photo(url = "https://live.staticflickr.com/65535/54969116967_e07192ef17.jpg")
                ),
                total = 999
            ),
    )

    val SEARCH_STATE = SUCCESS_STATE.copy(
        searchText = mutableStateOf("spiderman")
    )
}