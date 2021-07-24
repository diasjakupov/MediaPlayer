package com.example.mediaplayer.ui.fragments.playlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.db.entites.AudioEntity
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    val repository: Repository
): ViewModel(){
    val playlist: LiveData<List<PlaylistEntity>> get()= repository.playlists

    fun createNewPlaylist(text: String, audio:AudioInfo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createNewPlaylist(PlaylistEntity(text))
            val playlistId=repository.getLastCreatedPlaylist().id
            createNewAudioEntity(audio, playlistId.toInt())
        }
    }

    fun createNewAudioEntity(audio: AudioInfo, playlistId:Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.createNewAudioEntity(AudioEntity(audio, playlistId))
        }
    }
}