package com.example.mediaplayer.ui.activity

import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.ActivityVideoDetailInfoBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_video_detail_info.*
import kotlinx.android.synthetic.main.activity_video_detail_info.customToolBar

class VideoDetailInfoActivity : AppCompatActivity() {
    private var _binding: ActivityVideoDetailInfoBinding? = null
    private val args: VideoDetailInfoActivityArgs by navArgs()
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoDetailInfoBinding.inflate(layoutInflater)
        binding.video = args.video
        setContentView(binding.root)
        setSupportActionBar(customToolBar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}