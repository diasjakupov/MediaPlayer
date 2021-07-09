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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.utils.convertDuration
import com.example.mediaplayer.ui.activity.services.AudioPlayerService
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
    private val args: AudioPlayerActivityArgs by navArgs()
    @Inject
    lateinit var trackSelector: DefaultTrackSelector
    private lateinit var mService: AudioPlayerService
    private var mBound: Boolean = false
    private var mPlayer: SimpleExoPlayer?=null
    private lateinit var playerView: PlayerView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        playerView=findViewById(R.id.audio_player_view)

        setSupportActionBar(audioToolbar)

        setData()

        val mConnection=object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val localBinder=service as AudioPlayerService.LocalBinder
                mService=localBinder.service
                mBound=true

                initializePlayer()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mBound = false
            }

        }

        val intent=Intent(this, AudioPlayerService::class.java).apply {
            bindService(this, mConnection, Context.BIND_AUTO_CREATE)
        }
        intent.putExtra("AUDIO_INFO", args.audio)
        Util.startForegroundService(this, intent)

    }

    private fun initializePlayer(){
        if(mBound){
            mPlayer=mService.player
            playerView.player=mPlayer
            playerView.showController()
            Log.e("TAG", mPlayer?.currentPosition.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(){
        val formattedDuration=args.audio.duration!!.convertDuration()
        supportActionBar?.title=args.audio.title
        supportActionBar?.subtitle= "${args.audio.author}:  $formattedDuration"
    }
}