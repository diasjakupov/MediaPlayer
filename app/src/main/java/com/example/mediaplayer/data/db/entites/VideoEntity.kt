package com.example.mediaplayer.data.db.entites

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mediaplayer.data.models.video.CustomFormatOfTrack

@Entity(tableName = "viewed_video")
data class VideoEntity(
    @PrimaryKey val contentUri: String,
    val name:String?,
    val viewedTime: Long?,
    val wholeDuration: Long?,
    val realPath:String,
    @Embedded(prefix = "audio_") val selectedAudioTrack: CustomFormatOfTrack?,
    @Embedded(prefix = "subtitle") val selectedSubtitleTrack: CustomFormatOfTrack?
)
