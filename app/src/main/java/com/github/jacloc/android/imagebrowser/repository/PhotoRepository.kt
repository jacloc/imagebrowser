package com.github.jacloc.android.imagebrowser.repository

import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection

interface PhotoRepository {
    suspend fun getRecentPhotos(page: Int = getDefaultPageStartIndex()): Result<PhotoCollection>

    suspend fun searchPhotos(query: String, page: Int = getDefaultPageStartIndex()): Result<PhotoCollection>

    fun getDefaultPageStartIndex(): Int
}