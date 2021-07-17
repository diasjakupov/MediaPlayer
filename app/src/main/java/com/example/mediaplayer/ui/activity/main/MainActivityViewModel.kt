package com.example.mediaplayer.ui.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    fun startGettingData()=viewModelScope.launch(Dispatchers.Default){
        repository.getVideoList()
        repository.getAudioList()
    }
}