package com.github.jacloc.android.imagebrowser.repository

import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.API_KEY
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.FORMAT_JSON
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.METHOD_RECENT_PHOTOS
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.METHOD_SEARCH
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.NO_JSON_CALLBACK
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_API_KEY
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_FORMAT
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_METHOD
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_NO_JSON_CALLBACK
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_PAGE
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_PER_PAGE
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_SEARCH_TEXT
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PER_PAGE_DEFAULT
import com.github.jacloc.android.imagebrowser.mappers.FlickrPhotosResponseMapper
import com.github.jacloc.android.imagebrowser.mocks.MockPhotoCollection
import com.github.jacloc.android.imagebrowser.mocks.MockPhotosResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.stub
import kotlin.test.Test

class FlickrPhotoRepositoryTest {

    @Mock
    lateinit var flickrApi: FlickrApi

    @Mock
    lateinit var flickrPhotosResponseMapper: FlickrPhotosResponseMapper

    private val photosResponse = MockPhotosResponse.basic()
    private val photoCollection = MockPhotoCollection.basic()

    private lateinit var target: FlickrPhotoRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        target = FlickrPhotoRepository(
            flickrApi = flickrApi,
            flickrPhotosResponseMapper = flickrPhotosResponseMapper,
            ioDispatcher = UnconfinedTestDispatcher()
        )
        flickrPhotosResponseMapper.stub {
            on { map(photosResponse) }.thenAnswer { photoCollection }
        }
    }

    @Test
    fun testGetRecentPhotos() = runTest {
        val expectedQueryMap = mapOf(
            PARAM_NAME_API_KEY to API_KEY,
            PARAM_NAME_FORMAT to FORMAT_JSON,
            PARAM_NAME_NO_JSON_CALLBACK to NO_JSON_CALLBACK,
            PARAM_NAME_PER_PAGE to PER_PAGE_DEFAULT.toString(),
            PARAM_NAME_METHOD to METHOD_RECENT_PHOTOS,
            PARAM_NAME_PAGE to "1"
        )

        flickrApi.stub {
            onBlocking { fetchPhotos(expectedQueryMap) }.thenAnswer { photosResponse }
        }

        val recentPhotos = target.getRecentPhotos()

        assertThat(recentPhotos, `is`(Result.success(photoCollection)))
    }

    @Test
    fun testSearchPhotos() = runTest {
        val searchText = "spiderman"
        val expectedQueryMap = mapOf(
            PARAM_NAME_API_KEY to API_KEY,
            PARAM_NAME_FORMAT to FORMAT_JSON,
            PARAM_NAME_NO_JSON_CALLBACK to NO_JSON_CALLBACK,
            PARAM_NAME_PER_PAGE to PER_PAGE_DEFAULT.toString(),
            PARAM_NAME_METHOD to METHOD_SEARCH,
            PARAM_NAME_SEARCH_TEXT to searchText,
            PARAM_NAME_PAGE to "1"
        )

        flickrApi.stub {
            onBlocking { fetchPhotos(expectedQueryMap) }.thenAnswer { photosResponse }
        }

        val searchPhotos = target.searchPhotos(searchText)

        assertThat(searchPhotos, `is`(Result.success(photoCollection)))
    }
}