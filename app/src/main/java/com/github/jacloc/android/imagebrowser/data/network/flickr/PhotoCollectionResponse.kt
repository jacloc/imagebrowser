package com.github.jacloc.android.imagebrowser.data.network.flickr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoCollectionResponse(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val photo: List<PhotoResponse>,
    val total: Int
)
