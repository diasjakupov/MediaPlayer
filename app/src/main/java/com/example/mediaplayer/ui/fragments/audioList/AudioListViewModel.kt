package com.example.mediaplayer.ui.fragments.audioList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val repository: Repository,
    app: Application
) : AndroidViewModel(app) {
    val audioList = repository.audioList as MutableLiveData

    fun getAudioList() = viewModelScope.launch(Dispatchers.IO) {
        repository.getAudioList()
    }
}