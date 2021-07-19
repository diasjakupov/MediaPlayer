package com.example.mediaplayer.data.db.datasource

import android.util.Log
import com.example.mediaplayer.data.db.dao.PlaylistDao
import com.example.mediaplayer.data.db.dao.VideoDao
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.data.db.entites.VideoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    private val videoDao: VideoDao,
    private val playlistDao: PlaylistDao
){

    fun getVideoList(): Flow<List<VideoEntity>>{
        return videoDao.getViewedVideos()
    }

    fun getPlaylists(): Flow<List<PlaylistEntity>>{
        return playlistDao.getPlaylistEntity()
    }

    suspend fun insertOrUpdateVideoEntity(entity: VideoEntity){
        videoDao.insertOrUpdateVideoEntity(entity)
    }

    suspend fun deleteVideoEntity(uri:String){
        videoDao.deleteVideoByUri(uri)
    }
}