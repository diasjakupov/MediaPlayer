package com.example.mediaplayer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mediaplayer.data.db.dao.PlaylistDao
import com.example.mediaplayer.data.db.dao.VideoDao
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.data.db.entites.VideoEntity

@Database(entities = [VideoEntity::class, PlaylistEntity::class], version = 1, exportSchema = false)
abstract class MediaPlayerDataBase: RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun playlistDao(): PlaylistDao
}