package com.example.mediaplayer.data.models

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Video(
    val uri: Uri,
    val name:String,
    val duration: Int,
    val size: Int
): Parcelable
