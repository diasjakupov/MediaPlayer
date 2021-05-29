package com.example.mediaplayer.data.repository

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.providers.VideoProvider
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Repository @Inject constructor(
    private val videoProvider: VideoProvider,
    private val app: Application
){
    private val _videoList= MutableLiveData<ArrayList<Video>>()
    val videoList: LiveData<ArrayList<Video>> = _videoList

    private fun checkPermission():Boolean{
        return ContextCompat.checkSelfPermission(app.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun getVideoList(){
        if(checkPermission()){
            val data=videoProvider.getVideoList()
            _videoList.value=data
        }
    }

}