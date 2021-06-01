package com.example.mediaplayer.ui.fragments.videoList

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.mediaplayer.data.models.Video
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
    val videoList = repository.videoList
    val searchedList = MutableLiveData<List<Video>?>()

    fun getVideoList() = repository.getVideoList()

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


}