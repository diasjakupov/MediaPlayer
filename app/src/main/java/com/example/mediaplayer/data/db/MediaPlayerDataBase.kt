package com.example.mediaplayer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mediaplayer.data.db.dao.VideoDao
import com.example.mediaplayer.data.db.entites.VideoEntity

@Database(entities = [VideoEntity::class], version = 1, exportSchema = false)
abstract class MediaPlayerDataBase: RoomDatabase() {
    abstract fun videoDao(): VideoDao
}