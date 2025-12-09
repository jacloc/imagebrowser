package com.github.jacloc.android.imagebrowser.viewmodel

import app.cash.turbine.test
import com.github.jacloc.android.imagebrowser.helpers.MainDispatcherRule
import com.github.jacloc.android.imagebrowser.repository.PhotoRepository
import com.github.jacloc.android.imagebrowser.mocks.MockPhotoCollection
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.stub

class PhotoBrowserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var photoRepository: PhotoRepository

    private lateinit var target: PhotoBrowserViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testInitialLoad() = runTest {
        val recentPhotos = MockPhotoCollection.basic()

        photoRepository.stub {
            onBlocking { getRecentPhotos() }.thenAnswer { recentPhotos }
        }

        target = PhotoBrowserViewModel(photoRepository)
        target.uiStateFlow.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading, `is`(false))
            assertThat(initialState.photoCollection, `is`(recentPhotos))
        }
    }

    @Test
    fun testSearch() = runTest {
        val recentPhotos = MockPhotoCollection.basic()
        val searchPhotos = MockPhotoCollection.basic(total = 3_000_000)
        val searchText = "spiderman"

        photoRepository.stub {
            onBlocking { getRecentPhotos() }.thenAnswer { recentPhotos }
            onBlocking { searchPhotos(searchText) }.thenAnswer { searchPhotos }
        }

        target = PhotoBrowserViewModel(photoRepository)
        target.search(searchText)
        target.uiStateFlow.test {
            val searchState = awaitItem()
            assertThat(searchState.isLoading, `is`(false))
            assertThat(searchState.photoCollection, `is`(searchPhotos))
        }
    }

    @Test
    fun testLoadMore() = runTest {
        val recentPhotos = MockPhotoCollection.basic()
        val searchPhotos = MockPhotoCollection.basic(total = 3_000_000)
        val searchPhotosNextPage = MockPhotoCollection.basic(currentPage = 2, total = 3_000_000)
        val searchText = "spiderman"

        photoRepository.stub {
            onBlocking { getRecentPhotos() }.thenAnswer { recentPhotos }
        }

        photoRepository.stub {
            onBlocking { searchPhotos(searchText) }.thenAnswer { searchPhotos }
            onBlocking { searchPhotos(searchText, 2) }.thenAnswer {
                searchPhotosNextPage
            }
        }

        target = PhotoBrowserViewModel(photoRepository)
        target.uiStateFlow.test {
            // Await initial state
            val initial = awaitItem()
            assertThat(initial.isLoading, `is`(false))

            // When searching
            target.uiStateFlow.value.searchText.value = searchText
            target.search(searchText)

            // Await loading and verify
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading, `is`(true))
            // Await search results and verify
            val searchResultState = awaitItem()
            assertThat(searchResultState.isLoading, `is`(false))

            // When loading more
            target.loadMorePhotos()
            val loadMoreState = awaitItem()
            // Then verify the state has combined two pages of photos
            assertThat(loadMoreState.isLoadingMore, `is`(false))
            assertThat(loadMoreState.photoCollection, `is`(
                searchPhotos.copy(
                    currentPage = 2,
                    photoList = buildList {
                        addAll(searchPhotos.photoList)
                        addAll(searchPhotosNextPage.photoList)
                    }
                )
            ))
        }
    }
}