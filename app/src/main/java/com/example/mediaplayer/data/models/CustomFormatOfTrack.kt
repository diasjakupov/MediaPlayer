package com.example.mediaplayer.data.models

import android.os.Parcelable
import com.google.android.exoplayer2.Format
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CustomFormatOfTrack (
    val format: Format,
    val groupIndex: Int,
    val selectedType: Int
) : Parcelable