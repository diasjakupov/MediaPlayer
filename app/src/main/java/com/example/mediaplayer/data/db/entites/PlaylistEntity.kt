package com.example.mediaplayer.data.db.entites

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "playlist")
@Parcelize
data class PlaylistEntity(
    val name: String,
    @PrimaryKey(autoGenerate = true) var id: Int=0
): Parcelable