package com.example.mediaplayer.ui.fragments.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaplayer.data.models.CustomFormatOfTrack
import com.google.android.exoplayer2.Format
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(): ViewModel() {
    var videoSpeed=MutableLiveData(1.0f)
    var videoLanguage=MutableLiveData<CustomFormatOfTrack>()
    var videoSubtitle=MutableLiveData<CustomFormatOfTrack>()
}