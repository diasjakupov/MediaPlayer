package com.example.mediaplayer.ui.activity.videoDetailInfo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.ActivityVideoDetailInfoBinding
import com.example.mediaplayer.ui.activity.player.VideoPlayerActivity
import com.example.mediaplayer.ui.adapters.VideoItemInfoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_video_detail_info.customToolBar
import javax.inject.Inject

@AndroidEntryPoint
class VideoDetailInfoActivity : AppCompatActivity() {
    private var _binding: ActivityVideoDetailInfoBinding? = null
    private val args: VideoDetailInfoActivityArgs by navArgs()
    private val binding get() = _binding!!
    @Inject
    lateinit var audioAdapter: VideoItemInfoAdapter
    @Inject lateinit var subTitleAdapter: VideoItemInfoAdapter
    private val viewModel: VideoDetailInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoDetailInfoBinding.inflate(layoutInflater)
        binding.video = args.video

        setContentView(binding.root)
        setSupportActionBar(customToolBar)
        supportActionBar?.title = args.video.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.videoAudioTrackInfoRV.adapter = audioAdapter
        binding.videoAudioTrackInfoRV.layoutManager = LinearLayoutManager(this)
        viewModel.getExtraDataList(this, args.video.contentUri)

        viewModel.extraDataList.observe(this, {
            if (it != null) {
                audioAdapter.updateDataList(it)
            }
        })

        binding.flButton.setOnClickListener {
            val intent=Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_INFO", args.video)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}