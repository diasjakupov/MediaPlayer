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
import com.example.mediaplayer.data.db.datasource.LocalDataSource
import com.example.mediaplayer.data.db.entites.VideoEntity
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.providers.AudioProvider
import com.example.mediaplayer.data.providers.VideoProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.collections.ArrayList


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val videoProvider: VideoProvider,
    private val audioProvider: AudioProvider,
    private val app: Application,
    private val localDataSource: LocalDataSource
) {

    private val _videoList = MutableLiveData<ArrayList<VideoInfo>>()
    val videoList: LiveData<ArrayList<VideoInfo>> = _videoList
    private var isVideoParsingEnded=false

    private val _audioList = MutableLiveData<ArrayList<AudioInfo>>()
    val audioList: LiveData<ArrayList<AudioInfo>> = _audioList
    private var isAudioParsingEnded=false

    val viewedVideoList=localDataSource.getVideoList().asLiveData()

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
            val temporaryVideoStorage= arrayListOf<VideoInfo>()
            if(!isVideoParsingEnded){
                videoProvider.getVideoList().collect {list->
                    temporaryVideoStorage.addAll(list)
                    _videoList.postValue(temporaryVideoStorage)
                }
                isVideoParsingEnded=true
            }
        }
    }

    suspend fun getAudioList() {
        if (checkReadingPermission()) {
            val temporaryAudioStorage= arrayListOf<AudioInfo>()
            if(!isAudioParsingEnded){
                audioProvider.getAudioList().collect {list->
                    temporaryAudioStorage.addAll(list)
                    _audioList.postValue(temporaryAudioStorage)
                }
                isAudioParsingEnded=true
            }
        }
    }

    fun deleteVideoByUri(video: VideoInfo): IntentSender?{
        if(checkWritingPermission()){
            return videoProvider.deleteMediaByUri(video.contentUri)
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