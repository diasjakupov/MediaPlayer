package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.utils.VideoDiffUtils
import com.example.mediaplayer.databinding.VideoItemLayoutBinding
import com.example.mediaplayer.ui.fragments.videoList.VideoListFragmentDirections
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideoListAdapter @Inject constructor() : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private var videoList = emptyList<Video>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun updateDataList(newList: List<Video>) {
        val videoDiffUtil = VideoDiffUtils(videoList, newList)
        val result = DiffUtil.calculateDiff(videoDiffUtil)
        videoList = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val binding: VideoItemLayoutBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.video = video
            try {
                val thumbnail =
                    context.contentResolver.loadThumbnail(video.uri, Size(400, 150), null)
                Glide.with(context).load(thumbnail).into(binding.videoThumbnail)
            } catch (e: Exception) {
                Glide.with(context).load(R.drawable.ic_error).into(binding.videoThumbnail)
            }


            binding.videoItemDots.setOnClickListener {
                val action = VideoListFragmentDirections.actionVideoListToVideoInfoFragment(video)
                Log.e("TAG", "${video}")
                binding.root.findNavController().navigate(action)
            }
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VideoItemLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }
        }

    }
}