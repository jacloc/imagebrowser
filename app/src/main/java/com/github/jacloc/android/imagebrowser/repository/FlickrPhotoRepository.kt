package com.github.jacloc.android.imagebrowser.repository

import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi
import com.github.jacloc.android.imagebrowser.mappers.FlickrPhotosResponseMapper
import javax.inject.Inject

/**
 * PhotoRepository implementation that uses FlickrAPI
 */
class FlickrPhotoRepository @Inject constructor(
    private val flickrApi: FlickrApi,
    private val flickrPhotosResponseMapper: FlickrPhotosResponseMapper
) : PhotoRepository {

    override suspend fun getRecentPhotos(): Result<PhotoCollection> {
        val photosResponseResult = flickrApi.fetchPhotos(
            queryMap = createFlickrApiQueryMap(FlickrApi.METHOD_RECENT_PHOTOS)
        )

        return photosResponseResult.map(flickrPhotosResponseMapper::map)
    }

    override suspend fun searchPhotos(query: String): Result<PhotoCollection> {
        val photosResponseResult = flickrApi.fetchPhotos(
            queryMap = createFlickrApiQueryMap(
                method = FlickrApi.METHOD_SEARCH,
                searchText = query
            )
        )

        return photosResponseResult.map(flickrPhotosResponseMapper::map)
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
