package com.example.mediaplayer.data.repository

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.providers.VideoProvider
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val videoProvider: VideoProvider,
    private val app: Application
) {
    init {
        Log.e("TAG", "create repository singleton")
    }


    private val _videoList = MutableLiveData<ArrayList<Video>>()
    private var isParsingEnded=false
    val videoList: LiveData<ArrayList<Video>> = _videoList

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED
    }

    suspend fun getVideoList(isForced: Boolean = false) {
        if (checkPermission()) {
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

}