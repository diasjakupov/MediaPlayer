package com.example.mediaplayer.data.models

import android.net.Uri

abstract class CustomMediaInfo(
    open val contentUri: Uri,
    open val title: String?,
)

