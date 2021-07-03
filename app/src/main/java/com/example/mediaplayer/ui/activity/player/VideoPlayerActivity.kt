package com.example.mediaplayer.ui.activity.player

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.example.mediaplayer.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity() {
    //Initialize variables
    private lateinit var navController: NavController
    @Inject lateinit var trackSelectorUtil: ExoPlayerTrackSelection
    val args: VideoPlayerActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        navController=findNavController(R.id.playerNavHostFragment)
        navController.setGraph(R.navigation.player_nav, args.video)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "player destroy")
    }
}