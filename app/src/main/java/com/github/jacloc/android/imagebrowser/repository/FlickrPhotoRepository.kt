package com.github.jacloc.android.imagebrowser.repository

import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi
import com.github.jacloc.android.imagebrowser.coroutines.Dispatcher
import com.github.jacloc.android.imagebrowser.coroutines.ImageBrowserAppDispatchers
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

    override suspend fun getRecentPhotos(): Result<PhotoCollection> =
        withContext(ioDispatcher) {
            val photosResponseResult = flickrApi.fetchPhotos(
                queryMap = createFlickrApiQueryMap(FlickrApi.METHOD_RECENT_PHOTOS)
            )

            photosResponseResult.map(flickrPhotosResponseMapper::map)
        }

    override suspend fun searchPhotos(query: String): Result<PhotoCollection> =
        withContext(ioDispatcher) {
            val photosResponseResult = flickrApi.fetchPhotos(
                queryMap = createFlickrApiQueryMap(
                    method = FlickrApi.METHOD_SEARCH,
                    searchText = query
                )
            )

            photosResponseResult.map(flickrPhotosResponseMapper::map)
        }

    private fun createFlickrApiQueryMap(
        method: String,
        searchText: String? = null
    ): Map<String, String> = buildMap {
        putAll(FlickrApi.DEFAULT_QUERY_PARAMS)
        put(FlickrApi.PARAM_NAME_METHOD, method)
        if (searchText != null) {
            put(FlickrApi.PARAM_NAME_SEARCH_TEXT, searchText)
        }
    }
}
