package com.github.jacloc.android.imagebrowser.mocks

import com.github.jacloc.android.imagebrowser.data.network.flickr.PhotoResponse

object MockPhotoResponse {
    fun basic(): PhotoResponse =
        PhotoResponse(
            farm = 0,
            id = "54971698777",
            ispublic = 1,
            isfriend = 1,
            isfamily = 1,
            owner = "???",
            secret = "1b64ff4854",
            server = "65535",
            title = "_MG_7243"
        )
}