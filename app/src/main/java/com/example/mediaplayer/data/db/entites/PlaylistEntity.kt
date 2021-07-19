package com.example.mediaplayer.data.db.entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistEntity(
    val name: String
){
    @PrimaryKey(autoGenerate = true) var id: Int=0
}