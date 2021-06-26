package com.example.mediaplayer.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.mediaplayer.data.db.entites.VideoEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface VideoDao {

    @Query("SELECT * FROM viewed_video")
    fun getViewedVideos(): Flow<List<VideoEntity>>
}