package com.example.mediaplayer.ui.fragments.audioList

import android.app.Application
import android.content.IntentSender
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val repository: Repository,
    app: Application
) : AndroidViewModel(app) {
    val audioList = repository.audioList as MutableLiveData
    val searchedList = MutableLiveData<ArrayList<AudioInfo>?>()

    fun getAudioList() = viewModelScope.launch(Dispatchers.IO) {
        repository.getAudioList()
    }

    fun searchAudioList(search: String) {
        if (search.isNotEmpty()) {
            val data = audioList.value?.filter {
                it.title.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))
            } as ArrayList
            searchedList.value = data
        } else {
            searchedList.value = audioList.value
        }
    }

    fun updateAudioList(audio: AudioInfo){
        val newList=audioList.value?.filter {
            it.contentUri != audio.contentUri
        }
        audioList.value= newList as ArrayList<AudioInfo>?
    }
    fun deleteAudioFromStorage(audio: AudioInfo): IntentSender? {
        return repository.deleteAudioByUri(audio)
    }
}