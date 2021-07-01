package com.example.mediaplayer.data.models.video

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Video(
    val contentUri: Uri,
    val name:String?,
    val duration: Long?,
    val thumbnail:Bitmap?,
    val size: Long?,
    val quality: Int,
    val realPath:String,
    var viewedTime: Long?,
): Parcelable