package com.example.mediaplayer.data.models.audio

import android.net.Uri

data class Audio(
    val contentUri: Uri,
    val title: String,
    val duration: Long?,
    val size: Long?
)