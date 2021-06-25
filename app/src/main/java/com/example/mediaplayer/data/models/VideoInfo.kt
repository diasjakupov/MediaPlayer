package com.example.mediaplayer.data.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoInfo(
    val contentUri: Uri,
    val name:String?,
    val duration: Int?,
    val size: Long?,
    val quality:Int,
    val realPath:String
): Parcelable