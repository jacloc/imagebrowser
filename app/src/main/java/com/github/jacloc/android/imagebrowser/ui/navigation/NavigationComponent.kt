package com.github.jacloc.android.imagebrowser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.jacloc.android.imagebrowser.ui.screens.PhotoBrowserScreen
import com.github.jacloc.android.imagebrowser.viewmodel.PhotoBrowserViewModel
import kotlinx.serialization.Serializable

@Serializable
object PhotoBrowser

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = PhotoBrowser) {
        composable<PhotoBrowser> {
            val photoBrowserViewModel = hiltViewModel<PhotoBrowserViewModel>()
            PhotoBrowserScreen(photoBrowserViewModel)
        }
    }
}