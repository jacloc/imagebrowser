package com.github.jacloc.android.imagebrowser.repository

import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi
import com.github.jacloc.android.imagebrowser.coroutines.Dispatcher
import com.github.jacloc.android.imagebrowser.coroutines.ImageBrowserAppDispatchers
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.API_KEY
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.FORMAT_JSON
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.NO_JSON_CALLBACK
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_API_KEY
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_FORMAT
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi.Companion.PARAM_NAME_NO_JSON_CALLBACK
import com.github.jacloc.android.imagebrowser.mappers.FlickrPhotosResponseMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * PhotoRepository implementation that uses FlickrAPI
 */
class FlickrPhotoRepository @Inject constructor(
    private val flickrApi: FlickrApi,
    private val flickrPhotosResponseMapper: FlickrPhotosResponseMapper,
    @Dispatcher(ImageBrowserAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : PhotoRepository {

    override suspend fun getRecentPhotos(page: Int): Result<PhotoCollection> =
        withContext(ioDispatcher) {
            val photosResponseResult = flickrApi.fetchPhotos(
                queryMap = createFlickrApiQueryMap(
                    method = FlickrApi.METHOD_RECENT_PHOTOS,
                    page = page
                )
            )

            photosResponseResult.map(flickrPhotosResponseMapper::map)
        }

    override suspend fun searchPhotos(query: String, page: Int): Result<PhotoCollection> =
        withContext(ioDispatcher) {
            val photosResponseResult = flickrApi.fetchPhotos(
                queryMap = createFlickrApiQueryMap(
                    method = FlickrApi.METHOD_SEARCH,
                    searchText = query,
                    page = page
                )
            )

            photosResponseResult.map(flickrPhotosResponseMapper::map)
        }

    override fun getDefaultPageStartIndex(): Int =
        FlickrApi.PAGE_DEFAULT_START_INDEX

    private fun createFlickrApiQueryMap(
        method: String,
        searchText: String? = null,
        page: Int,
    ): Map<String, String> = buildMap {
        put(PARAM_NAME_API_KEY, API_KEY)
        put(PARAM_NAME_FORMAT, FORMAT_JSON)
        put(PARAM_NAME_NO_JSON_CALLBACK, NO_JSON_CALLBACK)
        put(FlickrApi.PARAM_NAME_METHOD, method)
        if (searchText != null) {
            put(FlickrApi.PARAM_NAME_SEARCH_TEXT, searchText)
        }
        put(FlickrApi.PARAM_NAME_PAGE, page.toString())
    }
}
