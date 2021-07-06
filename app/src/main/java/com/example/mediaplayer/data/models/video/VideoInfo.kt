package com.example.mediaplayer.data.models.video

import android.net.Uri
import android.os.Parcelable
import com.example.mediaplayer.data.models.CustomMediaInfo
import kotlinx.android.parcel.Parcelize
import java.time.Duration

@Parcelize
data class VideoInfo(
    override val contentUri: Uri,
    override val title:String?,
    val duration: Long?,
    val size: Long?,
    val quality:Int,
    val realPath:String,
    var viewedTime: Long?
): Parcelable, CustomMediaInfo(contentUri, title)