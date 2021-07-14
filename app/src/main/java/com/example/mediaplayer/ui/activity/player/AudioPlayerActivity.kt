package com.example.mediaplayer.ui.activity.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.telecom.ConnectionService
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.utils.convertDuration
import com.example.mediaplayer.ui.activity.services.AudioPlayerService
import com.example.mediaplayer.ui.fragments.audioList.AudioListViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_audio_player.*
import kotlinx.android.synthetic.main.fragment_player.*
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerActivity : AppCompatActivity() {
    @Inject
    lateinit var trackSelector: DefaultTrackSelector

    private val viewModel: AudioPlayerViewModel by viewModels()

    var notificationArgs: AudioInfo? = null
    private lateinit var mService: AudioPlayerService
    private var mBound: Boolean = false
    private lateinit var playerView: PlayerView
    private lateinit var audioRepeatMode: ImageView


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        playerView = findViewById(R.id.audio_player_view)
        audioRepeatMode = findViewById(R.id.audioRepeatMode)

        setSupportActionBar(audioToolbar)
        val mConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val localBinder = service as AudioPlayerService.LocalBinder
                mService = localBinder.service
                mBound = true
                initializePlayer()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mBound = false
            }

        }
        Intent(this, AudioPlayerService::class.java).apply {
            bindService(this, mConnection, Context.BIND_AUTO_CREATE)
        }
        initializePlayer()

        viewModel.playedAudio.observe(this, {
            setData(it)
        })
    }

    private fun initializePlayer() {
        if (mBound) {
            playerView.player = mService.player
            playerView.showController()
            changeRepeatMode()
        }
    }

    private fun changeRepeatMode(){
        audioRepeatMode.setOnClickListener {
            when(mService.player.repeatMode){
                Player.REPEAT_MODE_OFF->{
                    audioRepeatMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_icon_repeat_all))
                    mService.player.repeatMode=Player.REPEAT_MODE_ALL
                }
                Player.REPEAT_MODE_ALL->{
                    audioRepeatMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_icon_repeat_one))
                    mService.player.repeatMode=Player.REPEAT_MODE_ONE
                }
                Player.REPEAT_MODE_ONE->{
                    audioRepeatMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_icon_repeat_off))
                    mService.player.repeatMode=Player.REPEAT_MODE_OFF
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(audio: AudioInfo) {
        val formattedDuration = audio.duration!!.convertDuration()
        supportActionBar?.title = audio.title
        supportActionBar?.subtitle = "${audio.author}:  $formattedDuration"
    }
}