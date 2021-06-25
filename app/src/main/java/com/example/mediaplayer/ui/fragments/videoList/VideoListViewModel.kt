package com.example.mediaplayer.ui.fragments.videoList

import android.Manifest
import android.app.Application
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.providers.VideoProvider
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: Repository,
    app: Application
) : AndroidViewModel(app) {
    val videoList = repository.videoList as MutableLiveData
    val searchedList = MutableLiveData<List<Video>?>()

    fun getVideoList() = viewModelScope.launch(Dispatchers.IO) {
        repository.getVideoList()
    }


    fun updateVideoList(video:VideoInfo){
        val newList=videoList.value?.filter {
            it.contentUri != video.contentUri
        }
        videoList.value= newList as ArrayList<Video>?
    }

    fun searchVideoList(search: String) {
        if (search.isNotEmpty()) {
            val data = videoList.value?.filter {
                it.name?.toLowerCase(Locale.ROOT)!!.contains(search.toLowerCase(Locale.ROOT))
            }
            if (data != null) {
                searchedList.value = data
            }
        } else {
            searchedList.value = videoList.value
        }
    }

    fun deleteVideoByVideo(video: VideoInfo): IntentSender? {
        return repository.deleteVideoByUri(video)
    }
}


