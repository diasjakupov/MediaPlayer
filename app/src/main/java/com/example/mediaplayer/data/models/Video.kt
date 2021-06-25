package com.example.mediaplayer.data.models

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Video(
    val contentUri: Uri,
    val name:String?,
    val duration: Int?,
    val thumbnail:Bitmap?,
    val size: Long?,
    val quality: Int,
    val realPath:String
): Parcelable
