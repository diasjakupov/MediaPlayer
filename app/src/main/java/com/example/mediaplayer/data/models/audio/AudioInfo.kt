package com.example.mediaplayer.data.models.audio

import android.net.Uri

data class AudioInfo(
    val contentUri: Uri,
    val title: String,
    val duration: Long?,
    val size: Long?,
    val author: String?,
    val albumUri: Uri,
    val realPAth: String?
)