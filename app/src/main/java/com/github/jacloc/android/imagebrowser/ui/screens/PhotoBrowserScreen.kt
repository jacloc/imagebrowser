package com.github.jacloc.android.imagebrowser.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoBrowserScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onSearch: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.fillMaxWidth(),
                        query = uiState.searchText.value,
                        onQueryChange = { uiState.searchText.value = it },
                        onSearch = onSearch,
                        expanded = false,
                        onExpandedChange = {},
                        leadingIcon = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Search, "Search")
                            }
                        },
                        trailingIcon = {},
                    )
                },
                expanded = false,
                onExpandedChange = {},
                content = {}
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
                CircularProgressIndicator(modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .wrapContentSize())
            } else if (uiState.photoCollection != null) {
                PhotoGrid(
                    modifier = Modifier.fillMaxSize(),
                    photoCollection = uiState.photoCollection
                )
            }
        }
    }
}

@Composable
fun PhotoGrid(
    modifier: Modifier = Modifier,
    photoCollection: PhotoCollection
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(3)
    ) {
        items(photoCollection.photoList) {
            PhotoItem(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), photo = it)
        }
    }
}

@Composable
fun PhotoItem(
    modifier: Modifier = Modifier,
    photo: Photo
) {
    AsyncImage(
        modifier = modifier,
        model = photo.url,
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
                onSearch = {}
            )
        }
    }
}