package com.github.jacloc.android.imagebrowser.mocks

import com.github.jacloc.android.imagebrowser.data.domain.Photo
import com.github.jacloc.android.imagebrowser.data.domain.PhotoCollection

object MockPhotoCollection {
    const val DEFAULT_PAGE_SIZE = 100
    const val DEFAULT_CURRENT_PAGE = 1
    const val DEFAULT_TOTAL = 1000
    const val DEFAULT_TOTAL_PAGES = 10

    fun basic(
        currentPage: Int = DEFAULT_CURRENT_PAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        total: Int = DEFAULT_TOTAL,
        totalPages: Int = DEFAULT_TOTAL_PAGES,
    ) = PhotoCollection(
        currentPage = currentPage,
        pageSize = pageSize,
        total = total,
        totalPages = totalPages,
        photoList = List(pageSize) {
            Photo(
                id = "$currentPage$it",
                title = "title$currentPage$it",
                url = "url$currentPage$it"
            )
        }
    )
}