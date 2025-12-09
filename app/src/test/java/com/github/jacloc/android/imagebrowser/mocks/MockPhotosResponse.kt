package com.github.jacloc.android.imagebrowser.mocks

import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoResponse
import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotosResponse

object MockPhotosResponse {
    fun basic(
        photoList: List<PhotoResponse> = listOf(MockPhotoResponse.basic())
    ): PhotosResponse =
        PhotosResponse(
            photoCollection = MockPhotoCollectionResponse.basic(photoList),
            stat = "ok"
        )
}