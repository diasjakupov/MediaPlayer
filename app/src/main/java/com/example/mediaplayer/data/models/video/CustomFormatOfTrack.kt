package com.example.mediaplayer.data.models.video

import android.os.Parcelable
import com.google.android.exoplayer2.Format
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CustomFormatOfTrack (
    val language: String?,
    val label: String?,
    val id: String?,
    val groupIndex: Int,
    val selectedType: Int
) : Parcelable