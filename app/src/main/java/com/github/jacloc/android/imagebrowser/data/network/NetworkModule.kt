package com.github.jacloc.android.imagebrowser.data.network

import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideFlickrApi(): FlickrApi {
        val networkJson = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(FlickrApi.Companion.BASE_URL)
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ResultCallAdapterFactory.Companion.create())
            .build()
            .create(FlickrApi::class.java)
    }

}