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
import com.example.mediaplayer.data.models.CustomMediaItemFormat
import com.example.mediaplayer.data.models.SubTitleInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailInfoViewModel @Inject constructor() : ViewModel() {

    private val _extraDataList = MutableLiveData<List<CustomMediaItemFormat>>()
    val extraDataList: LiveData<List<CustomMediaItemFormat>> get() = _extraDataList

    fun getExtraDataList(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val extractor = MediaExtractor()
            extractor.setDataSource(context, uri, hashMapOf())
            val trackCount = extractor.trackCount
            val data= mutableListOf<CustomMediaItemFormat>()
            for (i in 1 until trackCount) {
                val format = extractor.getTrackFormat(i)
                Log.e("TAG", "$format")
                if (format.getString(MediaFormat.KEY_MIME)!!.contains("audio")) {
                    data.add(
                        AudioInfo(format.getString(MediaFormat.KEY_LANGUAGE), "Audio Track")
                    )
                }
                else if (format.getString(MediaFormat.KEY_MIME)!!.contains("x-subrip")) {
                    Log.e("TAG", "$format")
                    data.add(
                        SubTitleInfo(format.getString(MediaFormat.KEY_LANGUAGE), "Subtitle Track")
                    )
                }
            }
            _extraDataList.postValue(data)
        }
    }
}