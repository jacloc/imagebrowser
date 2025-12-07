package com.github.jacloc.android.imagebrowser.data.network.flickr

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResponse(
    val farm: Int,
    val id: String,
    val isfamily: Int,
    val isfriend: Int,
    val ispublic: Int,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String,
)
