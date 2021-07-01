package com.example.mediaplayer.data.models.video

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.Duration

@Parcelize
data class VideoInfo(
    val contentUri: Uri,
    val name:String?,
    val duration: Long?,
    val size: Long?,
    val quality:Int,
    val realPath:String
): Parcelable