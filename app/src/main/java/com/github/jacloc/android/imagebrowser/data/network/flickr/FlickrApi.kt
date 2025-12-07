package com.github.jacloc.android.imagebrowser.data.network.flickr

import com.github.jacloc.android.imagebrowser.BuildConfig
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FlickrApi {
    @GET("?")
    suspend fun fetchPhotos(@QueryMap queryMap: Map<String, String>): Result<PhotosResponse>

    companion object {
        const val BASE_URL = "https://www.flickr.com/services/rest/"

        const val PARAM_NAME_METHOD = "method"
        const val METHOD_RECENT_PHOTOS = "flickr.photos.getRecent"
        const val METHOD_SEARCH = "flickr.photos.search"

        const val PARAM_NAME_NO_JSON_CALLBACK = "nojsoncallback"
        const val NO_JSON_CALLBACK = "1"

        const val PARAM_NAME_FORMAT = "format"
        const val FORMAT_JSON = "json"

        const val PARAM_NAME_API_KEY = "api_key"
        // In order to keep the key secret on public repo, use local.properties file to define API_KEY
        // For example in local.properties:  FLICKR_API_KEY=your_api_key_goes_here
        const val API_KEY = BuildConfig.FLICKR_API_KEY

        const val PARAM_NAME_SEARCH_TEXT = "text"

        // Shared default query params
        val DEFAULT_QUERY_PARAMS = mapOf<String, String>(
            PARAM_NAME_API_KEY to API_KEY,
            PARAM_NAME_FORMAT to FORMAT_JSON,
            PARAM_NAME_NO_JSON_CALLBACK to NO_JSON_CALLBACK
        )
    }
}