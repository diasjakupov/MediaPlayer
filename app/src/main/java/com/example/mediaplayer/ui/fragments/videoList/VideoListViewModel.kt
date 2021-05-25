package com.example.mediaplayer.ui.fragments.videoList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.providers.VideoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val videoProvider: VideoProvider,
    application: Application
): AndroidViewModel(application) {

    fun getVideo() = videoProvider.getVideoList()
}