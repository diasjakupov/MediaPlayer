package com.example.mediaplayer.ui.activity.videoDetailInfo

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.models.AudioInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailInfoViewModel @Inject constructor() : ViewModel() {

    private val _audioList = MutableLiveData<List<AudioInfo>>()
    val audioList: LiveData<List<AudioInfo>> get() = _audioList

    fun getAudioList(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val extractor = MediaExtractor()
            extractor.setDataSource(context, uri, hashMapOf())
            val trackCount = extractor.trackCount
            val audio= mutableListOf<AudioInfo>()
            for (i in 1 until trackCount) {
                val format = extractor.getTrackFormat(i)
                if(format.getString(MediaFormat.KEY_MIME)!!.contains("audio")){

                    audio.add(
                        AudioInfo(format.getString(MediaFormat.KEY_LANGUAGE)))
                }
            }
            _audioList.postValue(audio)
        }
    }
}