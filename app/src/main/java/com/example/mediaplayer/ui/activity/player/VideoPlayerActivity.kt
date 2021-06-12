package com.example.mediaplayer.ui.activity.player

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.utils.DoubleClickListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerActivity : AppCompatActivity() {
    //Initialize variables
    private lateinit var player:SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var rewBtn: ImageView
    private lateinit var ffwdBtn: ImageView
    private var args: VideoInfo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        args= intent.extras?.getParcelable("VIDEO_INFO")
        playerView=findViewById(R.id.playerView)
        progressBar=findViewById(R.id.progressBar)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onStart() {
        super.onStart()

        //initialize player
        player= SimpleExoPlayer
            .Builder(this)
            .build()

        playerView.player=player

        Log.e("TAG", "URI, ${args?.uri}")
        val defaultDataSource=DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaplayer"))
        val mediaItem=MediaItem
            .Builder()
            .setUri(args?.uri)
            .build()
        val mediaSource= ProgressiveMediaSource
            .Factory(defaultDataSource)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady=true
        player.repeatMode=Player.REPEAT_MODE_ALL

        //initialize player btn with custom behaviour
        rewBtn=playerView.findViewById(R.id.custom_rew)
        ffwdBtn=playerView.findViewById(R.id.custom_ffwd)

        rewBtn.setOnClickListener(object: DoubleClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDoubleClick(v: View) {
                updateCurrentPosition(rewBtn, -10000)
            }
        })

        ffwdBtn.setOnClickListener(object: DoubleClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDoubleClick(v: View) {
                updateCurrentPosition(rewBtn, 10000)
            }
        })
    }

    fun updateCurrentPosition(view:ImageView,update:Int){
        view.alpha= 1.0f
        player.seekTo(player.currentPosition+update)
    }

    override fun onStop() {
        super.onStop()
        playerView.player=null
        player.release()
    }
}