package com.example.mediaplayer.data.repository

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.mediaplayer.data.db.datasource.LocalDataSource
import com.example.mediaplayer.data.db.entites.AudioEntity
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.data.db.entites.VideoEntity
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.providers.AudioProvider
import com.example.mediaplayer.data.providers.VideoProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.collections.ArrayList


class Repository constructor(
    private val videoProvider: VideoProvider,
    private val audioProvider: AudioProvider,
    private val app: Context,
    private val localDataSource: LocalDataSource
) {

    private val _videoList = MutableLiveData<ArrayList<VideoInfo>>()
    val videoList: LiveData<ArrayList<VideoInfo>> = _videoList
    private var isVideoParsingEnded = false

    private val _audioList = MutableLiveData<ArrayList<AudioInfo>>()
    val audioList: LiveData<ArrayList<AudioInfo>> = _audioList
    private var isAudioParsingEnded = false

    val viewedVideoList = localDataSource.getVideoList().asLiveData()
    val playlists=localDataSource.getPlaylists().asLiveData()
    var audioPlaylist= MutableLiveData<List<AudioInfo>>()

    private val _playedAudio=MutableLiveData<AudioInfo>()
    val playedAudio:LiveData<AudioInfo> = _playedAudio

    //check permissions
    private fun checkReadingPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun checkWritingPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED
        return permission
    }

    //services
    fun setUpPlayedAudio(audio:AudioInfo){
        _playedAudio.value=audio
    }

    //external storage
    fun deleteVideoByUri(video: VideoInfo): IntentSender? {
        if (checkWritingPermission()) {
            return videoProvider.deleteMediaByUri(video.contentUri)
        } else {
            return null
        }
    }

    fun deleteAudioByUri(audio: AudioInfo): IntentSender? {
        if (checkWritingPermission()) {
            return videoProvider.deleteMediaByUri(audio.contentUri)
        } else {
            return null
        }
    }

    suspend fun getVideoList() {
        if (checkReadingPermission()) {
            val temporaryVideoStorage = arrayListOf<VideoInfo>()
            if (!isVideoParsingEnded) {
                videoProvider.getVideoList().collect { list ->
                    temporaryVideoStorage.addAll(list)
                    _videoList.postValue(temporaryVideoStorage)
                }
                isVideoParsingEnded = true
            }
        }
    }

    suspend fun getAudioList() {
        if (checkReadingPermission()) {
            val temporaryAudioStorage = arrayListOf<AudioInfo>()
            if (!isAudioParsingEnded) {
                audioProvider.getAudioList().collect { list ->
                    temporaryAudioStorage.addAll(list)
                    _audioList.postValue(temporaryAudioStorage)
                }
                isAudioParsingEnded = true
            }
        }
    }

    //database
    fun getPlaylistAudioEntities(id:Int): LiveData<List<AudioEntity>>{
        return localDataSource.getAudioList(id).asLiveData()
    }

    fun getLastCreatedPlaylist(): PlaylistEntity{
        return localDataSource.getLastCreatedPlaylist()
    }

    suspend fun createNewAudioEntity(audio:AudioEntity){
        localDataSource.createNewAudioEntity(audio)
    }

    suspend fun createNewPlaylist(playlist: PlaylistEntity){
        localDataSource.createNewPlaylist(playlist)
    }

    suspend fun updateOrCreateVideoEntity(videoEntity: VideoEntity) {
        localDataSource.insertOrUpdateVideoEntity(videoEntity)
    }

    suspend fun deleteVideoEntity(uri: Uri) {
        localDataSource.deleteVideoEntity(uri.toString())
    }
}