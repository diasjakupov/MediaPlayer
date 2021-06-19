package com.example.mediaplayer.ui.fragments.player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.utils.DoubleClickListener
import com.example.mediaplayer.ui.activity.player.ExoPlayerTrackSelection
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.video_player_controller.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerFragment : Fragment() {
    //Initialize variables
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var rewBtn: ImageView
    private lateinit var ffwdBtn: ImageView
    private lateinit var fullScreenBtn: ImageView
    private lateinit var pauseBtn: ImageView
    private lateinit var trackSelector: DefaultTrackSelector
    private var args: VideoInfo? = null
    private var isFullScreen: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_player, container, false)
        playerView = rootView.findViewById(R.id.playerView)
        progressBar = rootView.findViewById(R.id.progressBar)
        fullScreenBtn = rootView.findViewById(R.id.btnFullScreen)
        pauseBtn = rootView.findViewById(R.id.custom_pause)
        args=arguments?.getParcelable("VIDEO_INFO")
        return rootView
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onStart() {
        super.onStart()
        //initialize player
        trackSelector = DefaultTrackSelector(this.requireContext())
        val exoPlayerTrackSelector =
            ExoPlayerTrackSelection(trackSelector, trackSelector.parameters)

        player = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()

        playerView.player = player

        val defaultDataSource =
            DefaultDataSourceFactory(
                requireContext(),
                Util.getUserAgent(requireContext(), "mediaplayer")
            )

        val mediaItem = MediaItem
            .Builder()
            .setUri(args!!.uri)
            .build()

        val mediaSource = ProgressiveMediaSource
            .Factory(defaultDataSource)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL

        //initialize player btn with custom behaviour
        rewBtn = playerView.findViewById(R.id.custom_rew)
        ffwdBtn = playerView.findViewById(R.id.custom_ffwd)

        //add onClickListener on each btn
        rewBtn.setOnClickListener(object : DoubleClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDoubleClick(v: View) {
                updateCurrentPosition(rewBtn, -10000)
            }
        })

        ffwdBtn.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                updateCurrentPosition(ffwdBtn, 10000)
            }
        })

        pauseBtn.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                Log.e("TAG", "pause")
                player.playWhenReady = !player.playWhenReady
            }
        })

        btnFullScreen.setOnClickListener {
            if (isFullScreen) {
                btnFullScreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_fullscreen
                    )
                )
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isFullScreen = false
            } else {
                btnFullScreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_fullscreen_exit
                    )
                )
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                isFullScreen = true
            }
        }

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                } else if (state == Player.STATE_READY) {
                    progressBar.visibility = View.GONE
                }
            }
        })

    }


    private fun updateCurrentPosition(view: ImageView, update: Int) {
        view.alpha = 1.0f
        player.seekTo(player.currentPosition + update)
        lifecycleScope.launch {
            delay(1000)
            view.alpha = 0.0f
        }
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }
}