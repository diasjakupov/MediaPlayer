package com.example.mediaplayer.ui.activity.playlist

import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.PlaylistAudioActivityBinding
import com.example.mediaplayer.ui.adapters.AudioListAdapter
import com.example.mediaplayer.ui.fragments.audioList.AudioListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PlaylistAudioActivity : AppCompatActivity() {
    private val args: PlaylistAudioActivityArgs by navArgs()
    private val viewModel: AudioListViewModel by viewModels()
    lateinit var adapter: AudioListAdapter
    private lateinit var recyclerView: RecyclerView
    private var _binding: PlaylistAudioActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(
        savedInstanceState: Bundle?
    ){
        super.onCreate(savedInstanceState)
        _binding = PlaylistAudioActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //initialize variables
        recyclerView = binding.audioRV

        setSupportActionBar(binding.customToolBar)
        adapter= AudioListAdapter
            .Builder(this)
            .setIsPlaylistFlag(true)
            .build()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val playlist=args.playlist

        //set up UI data
        viewModel.getPlaylistAudioEntities(playlist.id).observe(this, {
            if( it != null){
                viewModel.updateAudioPlaylistInRepository(it)
                adapter.updateDataList(it.map { audio->audio.audio })
            }
        })

        supportActionBar?.title = args.playlist.name.capitalize(Locale.ROOT)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}