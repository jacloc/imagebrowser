package com.github.jacloc.android.imagebrowser.mocks

import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoCollectionResponse
import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoResponse

object MockPhotoCollectionResponse {
    fun basic(photoList: List<PhotoResponse>): PhotoCollectionResponse =
        MockPhotoCollection.basic().run {
            PhotoCollectionResponse(
                page = currentPage,
                pages = totalPages,
                perpage = pageSize,
                photo = photoList.map { MockPhotoResponse.basic() },
                total = total
            )
        }
}