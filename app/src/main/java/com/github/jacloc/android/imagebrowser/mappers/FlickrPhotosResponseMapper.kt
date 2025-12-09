package com.github.jacloc.android.imagebrowser.mappers

import androidx.annotation.VisibleForTesting
import com.github.jacloc.android.imagebrowser.data.domain.Photo
import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection
import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoCollectionResponse
import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoResponse
import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotosResponse
import javax.inject.Inject

/**
 * Maps Flickr network model [com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoResponse]
 * to domain model [com.github.jacloc.android.imagebrowser.data.domain.Photo]
 */
@VisibleForTesting
open class FlickrPhotosResponseMapper @Inject constructor() {

    @VisibleForTesting
    open fun map(photosResponse: PhotosResponse): PhotoCollection =
        mapPhotoCollectionResponse(photosResponse.photoCollection)

    private fun mapPhotoCollectionResponse(photoCollectionResponse: PhotoCollectionResponse): PhotoCollection =
        photoCollectionResponse.run {
            PhotoCollection(
                currentPage = page,
                totalPages = pages,
                pageSize = perpage,
                photoList = photo.map { mapSinglePhotoResponse(it) },
                total = total
            )
        }

    private fun mapSinglePhotoResponse(photoResponse: PhotoResponse): Photo =
        photoResponse.run {
            Photo(
                id = id,
                title = title,
                url = "https://live.staticflickr.com/$server/${id}_$secret.jpg"
            )
        }
}
