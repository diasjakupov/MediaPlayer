package com.example.mediaplayer.ui.activity.videoDetailInfo

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.ActivityVideoDetailInfoBinding
import com.example.mediaplayer.ui.adapters.VideoAudioTrackInfoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_video_detail_info.*
import kotlinx.android.synthetic.main.activity_video_detail_info.customToolBar
import javax.inject.Inject

@AndroidEntryPoint
class VideoDetailInfoActivity : AppCompatActivity() {
    private var _binding: ActivityVideoDetailInfoBinding? = null
    private val args: VideoDetailInfoActivityArgs by navArgs()
    private val binding get() = _binding!!
    @Inject
    lateinit var adapter: VideoAudioTrackInfoAdapter
    private val viewModel: VideoDetailInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoDetailInfoBinding.inflate(layoutInflater)
        binding.video = args.video

        setContentView(binding.root)
        setSupportActionBar(customToolBar)
        supportActionBar?.title = args.video.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.videoAudioTrackInfoRV.adapter = adapter
        binding.videoAudioTrackInfoRV.layoutManager = LinearLayoutManager(this)

        viewModel.getAudioList(this, args.video.uri)

        viewModel.audioList.observe(this, {
            if (it != null) {
                adapter.updateDataList(it)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}