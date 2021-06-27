package com.example.mediaplayer.ui.fragments.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.db.entites.VideoEntity
import com.example.mediaplayer.data.models.CustomFormatOfTrack
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.repository.Repository
import com.google.android.exoplayer2.Format
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    var videoSpeed=MutableLiveData(1.0f)
    var videoLanguage=MutableLiveData<CustomFormatOfTrack>()
    var videoSubtitle=MutableLiveData<CustomFormatOfTrack>()
    var videoStatus=MutableLiveData<Int>()
    val viewedVideoList=repository.viewedVideoList

    fun updateOrCreateVideoEntity(videoInfo: VideoInfo, audio: CustomFormatOfTrack?,  subtitle: CustomFormatOfTrack?,time: Long?){
        viewModelScope.launch(Dispatchers.IO){
            val entity=VideoEntity(
                videoInfo.contentUri.toString(),
                videoInfo.name, time,
                videoInfo.duration,
                videoInfo.realPath,
                audio,
                subtitle
            )
            repository.updateOrCreateVideoEntity(entity)
        }
    }
}