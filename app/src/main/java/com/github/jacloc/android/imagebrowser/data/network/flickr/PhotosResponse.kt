package com.github.jacloc.android.imagebrowser.data.network.flickr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponse(
    @SerialName("photos")
    val photoCollection: PhotoCollectionResponse,
    val stat: String
)
