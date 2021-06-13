package com.example.mediaplayer.ui.activity.player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.utils.DoubleClickListener
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.video_player_controller.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerActivity : AppCompatActivity() {
    //Initialize variables
    private lateinit var player:SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var rewBtn: ImageView
    private lateinit var ffwdBtn: ImageView
    private lateinit var fullScreenBtn: ImageView
    private lateinit var pauseBtn:ImageView
    private var args: VideoInfo? = null
    private var isFullScreen:Boolean=true
    private var savedPosition:Long=0L

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.e("TAG", "saving")
        outState.run {
            putLong("CURRENT_POSITION", savedPosition)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            savedPosition=getLong("CURRENT_POSITION")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        args= intent.extras?.getParcelable("VIDEO_INFO")
        playerView=findViewById(R.id.playerView)
        progressBar=findViewById(R.id.progressBar)
        fullScreenBtn=findViewById(R.id.btnFullScreen)
        pauseBtn=findViewById(R.id.custom_pause)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onStart() {
        super.onStart()

        //initialize player
        player= SimpleExoPlayer
            .Builder(this)
            .build()

        playerView.player=player

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

        //add onClickListener on each btn
        rewBtn.setOnClickListener(object: DoubleClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDoubleClick(v: View) {
                updateCurrentPosition(rewBtn, -10000)
            }
        })

        ffwdBtn.setOnClickListener(object: DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                updateCurrentPosition(ffwdBtn, 10000)
            }
        })

        pauseBtn.setOnClickListener(object: DoubleClickListener(){
            override fun onDoubleClick(v: View) {
                Log.e("TAG", "pause")
                player.playWhenReady=!player.playWhenReady
            }
        })

        btnFullScreen.setOnClickListener {
            if(isFullScreen){
                btnFullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen))
                requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isFullScreen=false
            }else{
                btnFullScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit))
                requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                isFullScreen=true
            }
        }

        player.addListener(object: Player.Listener{
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if(state==Player.STATE_BUFFERING){
                    progressBar.visibility=View.VISIBLE
                }else if(state==Player.STATE_READY){
                    progressBar.visibility=View.GONE
                }
            }
        })

        //check if there is a saved position
        if(savedPosition!=0L){
            player.seekTo(savedPosition)
        }
    }

    private fun updateCurrentPosition(view:ImageView,update:Int){
        view.alpha= 1.0f
        player.seekTo(player.currentPosition+update)
        lifecycleScope.launch {
            delay(1000)
            view.alpha=0.0f
        }
    }

    override fun onStop() {
        super.onStop()
        //save current position
        savedPosition=player.currentPosition
    }
}