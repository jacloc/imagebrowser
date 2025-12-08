package com.github.jacloc.android.imagebrowser.data.domain

data class PhotoCollection(
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val photoList: List<Photo>,
    val total: Int
) {
    companion object {
        val EMPTY = PhotoCollection(
            currentPage = 0,
            totalPages = 0,
            pageSize = 0,
            photoList = emptyList(),
            total = 0
        )
    }
}
