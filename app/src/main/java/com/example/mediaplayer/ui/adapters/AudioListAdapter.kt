package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.models.video.VideoAudioInfo
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.utils.MediaDiffUtils
import com.example.mediaplayer.ui.fragments.videoList.VideoListFragmentDirections
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class AudioListAdapter @Inject constructor(
    @ActivityContext val context: Context
) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    var videoList = emptyList<AudioInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.audio_item_layout, parent, false
        )
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun updateDataList(newList: List<AudioInfo>) {
        val videoDiffUtil = MediaDiffUtils(videoList, newList)
        val result = DiffUtil.calculateDiff(videoDiffUtil, true)
        videoList = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(audio: AudioInfo) {
            val image=itemView.findViewById<ImageView>(R.id.audioImage)
            val title=itemView.findViewById<TextView>(R.id.audioTitle)
            val author=itemView.findViewById<TextView>(R.id.audioAuthor)



//            Glide.with(context)
//                .load(ThumbnailUtils.createAudioThumbnail(File(audio.realPAth!!), Size(480, 480), null))
//                .error(ContextCompat.getDrawable(context, R.drawable.ic_error))
//                .into(image)
            title.text=audio.title
            author.text=audio.author
        }
    }
}