package com.github.jacloc.android.imagebrowser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.github.jacloc.android.imagebrowser.data.network.flickr.FlickrApi
import com.github.jacloc.android.imagebrowser.repository.PhotoRepository
import com.github.jacloc.android.imagebrowser.ui.theme.ImageBrowserTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var photoRepository: PhotoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val recentPhotosResult = photoRepository.getRecentPhotos()
            Log.i("TestGetPhotos", recentPhotosResult.toString())

            val searchResult = photoRepository.searchPhotos("spiderman")
            Log.i("TestSearchPhotos", searchResult.toString())
        }
        enableEdgeToEdge()
        setContent {
            ImageBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageBrowserTheme {
        Greeting("Android")
    }
}