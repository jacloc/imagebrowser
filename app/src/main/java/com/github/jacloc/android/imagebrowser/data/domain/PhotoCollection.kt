package com.github.jacloc.android.imagebrowser.data.domain

data class PhotoCollection(
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val photoList: List<Photo>,
    val total: Int
)
