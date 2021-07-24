package com.example.mediaplayer.data.db.entites

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mediaplayer.data.db.converters.UriConverter
import com.example.mediaplayer.data.models.audio.AudioInfo


@Entity(tableName = "audio")
@TypeConverters(UriConverter::class)
data class AudioEntity(
    @Embedded val audio: AudioInfo,
    val playlistId: Int
){
    @PrimaryKey(autoGenerate = true) var id: Int=0
}