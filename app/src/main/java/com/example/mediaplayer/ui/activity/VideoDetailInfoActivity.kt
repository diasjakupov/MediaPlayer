package com.example.mediaplayer.ui.activity

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
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

        val extractor=MediaExtractor()
        extractor.setDataSource(this, args.video.uri, hashMapOf())
        val trackCount=extractor.trackCount
        for (i in 1 until trackCount) {
            val format = extractor.getTrackFormat(i)
            Toast.makeText(this, "$format", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}