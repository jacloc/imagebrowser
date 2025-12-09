package com.github.jacloc.android.imagebrowser.mappers

import com.github.jacloc.android.imagebrowser.mocks.MockPhotosResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import kotlin.test.Test

class FlickrPhotosResponseMapperTest {

    private lateinit var target: FlickrPhotosResponseMapper

    @Before
    fun setup() {
        target = FlickrPhotosResponseMapper()
    }

    @Test
    fun testMapProfileUrl() {
        val expectedUrl = "https://live.staticflickr.com/65535/54971698777_1b64ff4854.jpg"
        val photoCollection = target.map(MockPhotosResponse.basic())
        assertThat(
            photoCollection.photoList.first().url,
            `is`(expectedUrl)
        )
    }
}