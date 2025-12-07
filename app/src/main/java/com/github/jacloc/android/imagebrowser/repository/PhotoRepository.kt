package com.github.jacloc.android.imagebrowser.repository

import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection

interface PhotoRepository {
    suspend fun getRecentPhotos(): Result<PhotoCollection>

    suspend fun searchPhotos(query: String): Result<PhotoCollection>
}