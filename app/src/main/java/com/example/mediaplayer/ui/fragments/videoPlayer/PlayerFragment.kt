package com.example.mediaplayer.ui.fragments.videoPlayer

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.utils.DoubleClickListener
import com.example.mediaplayer.data.utils.ifContains
import com.example.mediaplayer.data.utils.observeOnce
import com.example.mediaplayer.ui.activity.player.ExoPlayerTrackSelection
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.video_player_controller.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    //Initialize variables
    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var rewBtn: ImageView
    private lateinit var ffwdBtn: ImageView
    private lateinit var fullScreenBtn: ImageView
    private lateinit var pauseBtn: ImageView

    @Inject
    lateinit var trackSelector: DefaultTrackSelector

    @Inject
    lateinit var trackSelectorUtil: ExoPlayerTrackSelection
    private lateinit var audioSubtitleBtn: ImageView
    private var args: VideoInfo? = null
    private var isFullScreen: Boolean = true
    private val viewModel by activityViewModels<PlayerViewModel>()
    private lateinit var speedDialog: VideoSpeedFragment


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
        speedDialog = VideoSpeedFragment()
        audioSubtitleBtn = rootView.findViewById(R.id.audioSubtitleBtn)
        args = arguments?.getParcelable("VIDEO_INFO")

        //initialize player
        initializePlayer()

        //initialize player btn with custom behaviour
        rewBtn = playerView.findViewById(R.id.custom_rew)
        ffwdBtn = playerView.findViewById(R.id.custom_ffwd)

        //add onClickListener on each btn
        setOnClickListeners()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_BUFFERING) {
                    viewModel.videoStatus.value = state
                    progressBar.visibility = View.VISIBLE
                } else if (state == Player.STATE_READY) {
                    viewModel.videoStatus.value = state

                    //set settings for progressBar
                    progressBar.visibility = View.GONE

                    //setOnClickListeners on videostate-changing fragments
                    audioSubtitleBtn.setOnClickListener {
                        val action =
                            PlayerFragmentDirections.actionPlayerFragmentToVideoTrackSelection()
                        findNavController().navigate(action)
                    }

                    videoSpeedIcon.setOnClickListener {
                        if (!speedDialog.isVisible) {
                            speedDialog.show(childFragmentManager, "SHOW SPEED FRAGMENT")
                        }
                    }

                    //update videoSpeed
                    updateVideoSpeed()

                    //set default audiotrack
                    setDefaultAudioTrack()
                }
            }
        })

        //checking if video is in the database or if it should be updated
        checkVideo()
        return rootView
    }

    private fun setDefaultAudioTrack() {
        viewModel.viewedVideoList.observeOnce(viewLifecycleOwner, {
            val audio = trackSelectorUtil.getTrackFormat(C.TRACK_TYPE_AUDIO)
            if (!it.ifContains(args!!.contentUri)) {
                val videoLanguage = viewModel.videoLanguage
                if (videoLanguage.value == null) {
                    try {
                        val track = audio.first()
                        videoLanguage.value = track
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                val videoFromDb =
                    it.find { video -> Uri.parse(video.contentUri).path == args!!.contentUri.path }
                if (videoFromDb?.selectedAudioTrack != null) {
                    viewModel.videoLanguage.value = videoFromDb.selectedAudioTrack
                } else {
                    try {
                        val track = audio.first()
                        viewModel.videoLanguage.value = track
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (videoFromDb?.selectedSubtitleTrack != null) {
                    viewModel.videoSubtitle.value = videoFromDb.selectedSubtitleTrack
                }
            }
        })
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setOnClickListeners() {
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

        fullScreenBtn.setOnClickListener {
            if (isFullScreen) {
                fullScreenBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_fullscreen
                    )
                )
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isFullScreen = false
            } else {
                fullScreenBtn.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_fullscreen_exit
                    )
                )
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                isFullScreen = true
            }
        }
    }

    private fun initializePlayer() {
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
            .setUri(args!!.contentUri)
            .build()

        val mediaSource = ProgressiveMediaSource
            .Factory(defaultDataSource)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
    }

    private fun updateVideoSpeed() {
        viewModel.videoSpeed.observe(viewLifecycleOwner, {
            player.setPlaybackSpeed(it)
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

    override fun onPause() {
        super.onPause()
        Log.e("TAG", "pause")
        updateVideoEntity()
    }

    private fun checkVideo() {
        viewModel.viewedVideoList.observeOnce(viewLifecycleOwner, {
            if (it.isNullOrEmpty() || !it.ifContains(args!!.contentUri)) {
                updateVideoEntity()
            } else {
                val videoFromDb =
                    it.find { video -> Uri.parse(video.contentUri).path == args!!.contentUri.path }
                if (videoFromDb != null) {
                    videoFromDb.viewedTime?.let { viewed -> player.seekTo(viewed) }
                    viewModel.videoStatus.observeOnce(viewLifecycleOwner, {
                        videoFromDb.selectedAudioTrack?.let { selectedAudio ->
                            trackSelectorUtil.changeTrack(
                                index = selectedAudio.groupIndex,
                                selectedType = selectedAudio.selectedType
                            )
                        }
                        videoFromDb.selectedSubtitleTrack?.let { selectedSubtitle ->
                            trackSelectorUtil.changeTrack(
                                index = selectedSubtitle.groupIndex,
                                selectedType = selectedSubtitle.selectedType
                            )
                        }
                    })
                }
            }
        })
    }

    private fun updateVideoEntity() {
        viewModel.videoStatus.observe(viewLifecycleOwner, {
            if (it == Player.STATE_READY) {
                lifecycleScope.launch {
                    val audio = trackSelectorUtil.getSelectionOverride(C.TRACK_TYPE_AUDIO)
                    val subtitle = trackSelectorUtil.getSelectionOverride(C.TRACK_TYPE_TEXT)
                    viewModel.updateOrCreateVideoEntity(
                        args!!,
                        audio,
                        subtitle,
                        player.currentPosition
                    )
                }
            }

        })
    }
}