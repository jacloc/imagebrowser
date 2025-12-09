package com.github.jacloc.android.imagebrowser.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.jacloc.android.imagebrowser.data.domain.Photo
import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.ui.theme.ImageBrowserTheme
import com.github.jacloc.android.imagebrowser.viewmodel.PhotoBrowserViewModel
import com.github.jacloc.android.imagebrowser.viewmodel.UiState

@Composable
fun PhotoBrowserScreen(
    photoBrowserViewModel: PhotoBrowserViewModel
) {
    val uiState by photoBrowserViewModel.uiStateFlow.collectAsStateWithLifecycle()
    PhotoBrowserScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onSearch = { searchText ->
            photoBrowserViewModel.search(searchText)
        },
        onLoadMore = {
            photoBrowserViewModel.loadMorePhotos()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoBrowserScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.testTag(PhotoBrowserScreenTestTags.ERROR_SNACK_BAR),
                hostState = snackbarHostState
            )
        }
    ) {
        Column {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.fillMaxWidth().testTag(PhotoBrowserScreenTestTags.SEARCH_INPUT),
                        query = uiState.searchText.value,
                        onQueryChange = { uiState.searchText.value = it },
                        onSearch = {
                            keyboardController?.hide()
                            onSearch(it)
                        },
                        expanded = false,
                        onExpandedChange = {},
                        leadingIcon = {
                            Icon(Icons.Default.Search, "Search")
                        },
                        trailingIcon = {},
                    )
                },
                expanded = false,
                onExpandedChange = {},
                content = {},
            )

            if (uiState.errorMessage != null) {
                LaunchedEffect(uiState) {
                    snackbarHostState.showSnackbar(
                        message = uiState.errorMessage,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Long
                    )
                }
            }

            if (uiState.isLoading) {
                LoadingSpinner(PhotoBrowserScreenTestTags.LOADING_SPINNER)
            } else if (uiState.photoCollection != null) {
                PhotoGrid(
                    modifier = Modifier.fillMaxSize().testTag(PhotoBrowserScreenTestTags.PHOTO_GRID),
                    photoCollection = uiState.photoCollection,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = onLoadMore,
                )
            }
        }
    }
}

@Composable
fun LoadingSpinner(testTag: String = PhotoBrowserScreenTestTags.LOADING_SPINNER) {
    CircularProgressIndicator(modifier = Modifier
        .testTag(testTag)
        .padding(top = 8.dp)
        .fillMaxWidth()
        .wrapContentSize())
}

@Composable
fun PhotoGrid(
    modifier: Modifier = Modifier,
    photoCollection: PhotoCollection,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
) {
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    selectedPhoto?.let {
        EnlargedPhotoDialog(
            photo = it,
            onDismissRequest = {
                selectedPhoto = null
            }
        )
    }

    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        state = listState
    ) {
        items(photoCollection.photoList, key = { it.url }) {
            SquarePhotoItem(photo = it, onPhotoClicked = { p ->
                selectedPhoto = p
            })
        }
        if (isLoadingMore) {
            item(span = { GridItemSpan(3) }) {
                LoadingSpinner(PhotoBrowserScreenTestTags.LOAD_MORE_SPINNER)
            }
        }
    }

    // Check if more items need to be loaded
    val loadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex >= (totalItemsNumber - 9) && !isLoadingMore
        }
    }

    LaunchedEffect(loadMore) {
        if (loadMore) {
            onLoadMore()
        }
    }
}

@Composable
fun EnlargedPhotoDialog(photo: Photo, onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Allow full screen width
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable(onClick = {
                onDismissRequest()
            }),
            contentAlignment = Alignment.Center
        ) {
            DetailedPhotoItem(photo)
        }
    }
}

@Composable
fun DetailedPhotoItem(photo: Photo) {
    var showMetadata by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = {})
    ) {
        AsyncImage(
            model = photo.url,
            contentDescription = "",
            contentScale = ContentScale.Fit, // Fit the enlarged image within boundaries
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .clickable(onClick = { showMetadata = !showMetadata })
        )

        // Extra metadata about the photo
        AnimatedVisibility(showMetadata) {
            SelectionContainer {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "ID: ${photo.id}", style = MaterialTheme.typography.titleMedium)
                    if (photo.title.isNotBlank()) {
                        Text(text = "Title", style = MaterialTheme.typography.titleMedium)
                        Text(text = photo.title)
                    }
                    Text(text = "URL", style = MaterialTheme.typography.titleMedium)
                    Text(text = photo.url)
                }
            }
        }
    }
}

@Composable
fun SquarePhotoItem(
    photo: Photo,
    onPhotoClicked: (Photo) -> Unit
) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { onPhotoClicked(photo) })
            .aspectRatio(1f)
            .padding(8.dp),
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.url)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = "Image from URL",
    )
}

@Preview
@Composable
private fun PreviewPhotoBrowserScreen(
    @PreviewParameter(PhotoBrowserScreenPreviewParamProvider::class)
    uiState: UiState
) {
    ImageBrowserTheme {
        Surface {
            PhotoBrowserScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                onSearch = {},
                onLoadMore = {}
            )
        }
    }
}

object PhotoBrowserScreenTestTags {
    const val SEARCH_INPUT = "search_input"
    const val LOADING_SPINNER = "loading_spinner"
    const val LOAD_MORE_SPINNER = "load_more_spinner"
    const val PHOTO_GRID = "photo_grid"
    const val ERROR_SNACK_BAR = "error_snack_bar"
}