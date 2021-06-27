package com.example.mediaplayer.data.db.datasource

import com.example.mediaplayer.data.db.dao.VideoDao
import com.example.mediaplayer.data.db.entites.VideoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    private val videoDao: VideoDao
){

    fun getVideoList(): Flow<List<VideoEntity>>{
        return videoDao.getViewedVideos()
    }

    suspend fun insertOrUpdateVideoEntity(entity: VideoEntity){
        videoDao.insertOrUpdateVideoEntity(entity)
    }
}