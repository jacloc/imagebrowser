package com.github.jacloc.android.imagebrowser.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.jacloc.android.imagebrowser.data.domain.Photo
import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.viewmodel.UiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val PHOTO_COLLECTION_SIZE = 100

@RunWith(AndroidJUnit4::class)
class PhotoBrowserScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSuccessLayout() {
        composeTestRule.setContent {
            PhotoBrowserScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = UiState(
                    isLoading = false,
                    photoCollection = createPhotoCollection()
                ),
                onSearch = {},
                onLoadMore = {}
            )
        }

        with(composeTestRule) {
            onNodeWithTag(PhotoBrowserScreenTestTags.SEARCH_INPUT)
                .assertIsDisplayed()
                .assertTextEquals("")
            onNodeWithTag(PhotoBrowserScreenTestTags.LOADING_SPINNER)
                .assertIsNotDisplayed()
            onNodeWithTag(PhotoBrowserScreenTestTags.PHOTO_GRID)
                .assertIsDisplayed()
            onNodeWithTag(PhotoBrowserScreenTestTags.ERROR_SNACK_BAR)
                .assertIsNotDisplayed()
        }
    }

    @Test
    fun testLoadingLayout() {
        composeTestRule.setContent {
            PhotoBrowserScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = UiState(),
                onSearch = {},
                onLoadMore = {}
            )
        }

        with(composeTestRule) {
            onNodeWithTag(PhotoBrowserScreenTestTags.SEARCH_INPUT)
                .assertIsDisplayed()
                .assertTextEquals("")
            onNodeWithTag(PhotoBrowserScreenTestTags.LOADING_SPINNER)
                .assertIsDisplayed()
            onNodeWithTag(PhotoBrowserScreenTestTags.PHOTO_GRID)
                .assertIsNotDisplayed()
            onNodeWithTag(PhotoBrowserScreenTestTags.ERROR_SNACK_BAR)
                .assertIsNotDisplayed()
        }
    }

    @Test
    fun testErrorLayout() {
        val errorMessage = "Oopsie"
        composeTestRule.setContent {
            PhotoBrowserScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = UiState(
                    isLoading = false,
                    errorMessage = errorMessage,
                    photoCollection = createPhotoCollection()
                ),
                onSearch = {},
                onLoadMore = {}
            )
        }

        with(composeTestRule) {
            onNodeWithTag(PhotoBrowserScreenTestTags.SEARCH_INPUT)
                .assertIsDisplayed()
                .assertTextEquals("")
            onNodeWithTag(PhotoBrowserScreenTestTags.LOADING_SPINNER)
                .assertIsNotDisplayed()
            onNodeWithTag(PhotoBrowserScreenTestTags.PHOTO_GRID)
                .assertIsDisplayed()
            onNodeWithTag(PhotoBrowserScreenTestTags.ERROR_SNACK_BAR)
                .assertIsDisplayed()
            onNodeWithText(errorMessage)
                .assertIsDisplayed()
        }
    }

    private fun createPhotoCollection(): PhotoCollection =
        PhotoCollection(
            currentPage = 1,
            totalPages = 10,
            total = 1000,
            pageSize = PHOTO_COLLECTION_SIZE,
            photoList = List(PHOTO_COLLECTION_SIZE) {
                Photo(id = "$it", title = "title$it", url = "url$it")
            }
        )
}