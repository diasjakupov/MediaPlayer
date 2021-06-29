package com.example.mediaplayer.data.repository

import android.Manifest
import android.app.Application
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.mediaplayer.data.db.dao.VideoDao
import com.example.mediaplayer.data.db.datasource.LocalDataSource
import com.example.mediaplayer.data.db.entites.VideoEntity
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.providers.VideoProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.collections.ArrayList


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val videoProvider: VideoProvider,
    private val app: Application,
    private val localDataSource: LocalDataSource
) {

    private val _videoList = MutableLiveData<ArrayList<Video>>()
    private var isParsingEnded=false
    val viewedVideoList=localDataSource.getVideoList().asLiveData()
    val videoList: LiveData<ArrayList<Video>> = _videoList

    private fun checkReadingPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun checkWritingPermission(): Boolean {
        val permission=ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED
        Log.e("TAG", "perm $permission")
        return permission
    }

    suspend fun getVideoList() {
        if (checkReadingPermission()) {
            val temporaryVideosStorage= arrayListOf<Video>()
            if(!isParsingEnded){
                videoProvider.getVideoList().collect {list->
                    temporaryVideosStorage.addAll(list)
                    _videoList.postValue(temporaryVideosStorage)
                }
                isParsingEnded=true
            }
        }
    }

    fun deleteVideoByUri(video: VideoInfo): IntentSender?{
        if(checkWritingPermission()){
            return videoProvider.deleteVideoByUri(video)
        }else{
            return null
        }
    }

    suspend fun updateOrCreateVideoEntity(videoEntity: VideoEntity){
        localDataSource.insertOrUpdateVideoEntity(videoEntity)
    }

    suspend fun deleteVideoEntity(uri: Uri){
        localDataSource.deleteVideoEntity(uri.toString())
    }
}