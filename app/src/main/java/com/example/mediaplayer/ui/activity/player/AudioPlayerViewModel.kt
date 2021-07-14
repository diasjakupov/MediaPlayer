package com.example.mediaplayer.ui.activity.player

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    repository: Repository
): ViewModel() {
    val playedAudio:LiveData<AudioInfo> = repository.playedAudio
}