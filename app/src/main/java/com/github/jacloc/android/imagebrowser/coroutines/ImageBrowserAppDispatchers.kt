package com.github.jacloc.android.imagebrowser.coroutines

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val imageBrowserAppDispatchers: ImageBrowserAppDispatchers)

enum class ImageBrowserAppDispatchers {
    IO,
    Default
}
