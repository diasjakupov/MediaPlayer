package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.utils.VideoDiffUtils
import com.example.mediaplayer.databinding.VideoItemLayoutBinding
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideoListAdapter @Inject constructor(): RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private var videoList= emptyList<Video>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun updateDataList(newList: List<Video>){
        val videoDiffUtil=VideoDiffUtils(videoList, newList)
        val result=DiffUtil.calculateDiff(videoDiffUtil)
        videoList=newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val binding: VideoItemLayoutBinding, private val context: Context): RecyclerView.ViewHolder(binding.root){
        fun bind(video: Video){
            val thumbnail=context.contentResolver.loadThumbnail(video.uri, Size(400, 150), null)
            binding.video=video
           Glide.with(context).load(thumbnail).into(binding.videoThumbnail)
        }


        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val binding=VideoItemLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }
        }

    }
}