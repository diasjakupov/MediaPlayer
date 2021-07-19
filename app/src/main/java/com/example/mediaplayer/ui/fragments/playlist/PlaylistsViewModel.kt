package com.example.mediaplayer.ui.fragments.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    val repository: Repository
): ViewModel(){
    val playlist: LiveData<List<PlaylistEntity>> get()= repository.playlists
}