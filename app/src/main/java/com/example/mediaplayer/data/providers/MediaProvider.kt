package com.example.mediaplayer.data.providers

import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.mediaplayer.data.models.video.VideoInfo

interface MediaProvider {
    val selection: String?
    val selectionArgs: Array<String>
    val sortedOrder:String
    val collection: Uri

    fun provideMediaProjection(): Array<String>

    fun deleteMediaByUri(uri: Uri): IntentSender?
}