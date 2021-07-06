package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.utils.MediaDiffUtils
import com.example.mediaplayer.data.utils.doAsync
import com.example.mediaplayer.ui.fragments.videoList.VideoListFragmentDirections
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.delay
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class VideoListAdapter @Inject constructor(
    @ActivityContext val context: Context
) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    var videoList = emptyList<VideoInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.video_item_layout, parent, false
        )
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun updateDataList(newList: List<VideoInfo>) {
        val videoDiffUtil = MediaDiffUtils(videoList, newList)
        val result = DiffUtil.calculateDiff(videoDiffUtil, true)
        videoList = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(video: VideoInfo) {
            val title = itemView.findViewById<TextView>(R.id.videoTitle)
            val image = itemView.findViewById<ImageView>(R.id.videoThumbnail)
            val dots = itemView.findViewById<ImageView>(R.id.videoItemDots)
            val duration = itemView.findViewById<TextView>(R.id.videoDuration)
            val viewedDuration = itemView.findViewById<ImageView>(R.id.viewedDuration)
            val width = Resources.getSystem().displayMetrics.widthPixels

            val widthOfView = if (video.viewedTime != 0L) {
                (video.viewedTime?.toDouble()?.div(video.duration!!.toDouble())
                    ?.times(width.toDouble()))?.toInt()
            } else {
                1
            }
            //bind data to view
            Glide
                .with(context)
                .asBitmap()
                .load(video.contentUri)
                .error(R.drawable.ic_error)
                .into(image)



            title.text = video.title.toString()
            title.doAsync {
                delay(3000)
                it.isSelected=true
            }
            duration.text = convertDuration(video.duration!!)
            viewedDuration.layoutParams.width = widthOfView!!.toInt()
            //set up onClickListeners
            dots.setOnClickListener {
                val action =
                    VideoListFragmentDirections.actionVideoListToVideoInfoFragment(video)
                val navController = Navigation.findNavController(itemView)
                navController.navigate(action)
            }
            image.setOnClickListener {
                val action =
                    VideoListFragmentDirections.actionVideoListToVideoPlayerActivity(Bundle().apply {
                        putParcelable("VIDEO_INFO", video)
                    })
                val navController = Navigation.findNavController(itemView)
                navController.navigate(action)
            }
        }

        private fun convertDuration(data: Long): String {
            val initialSeconds = TimeUnit.MILLISECONDS.toSeconds(data).toInt()
            val hours = (initialSeconds / 3600)
            val minutes = abs(((hours * 3600 - initialSeconds) / 60))
            val seconds = abs((hours * 3600 + minutes * 60 - initialSeconds))
            val formatMinutes = if (minutes < 10) {
                "0$minutes"
            } else {
                minutes.toString()
            }
            val formatHours = if (hours < 10) {
                "0$hours"
            } else {
                hours.toString()
            }
            val formatSeconds = if (seconds < 10) {
                "0$seconds"
            } else {
                seconds.toString()
            }
            return "$formatHours:$formatMinutes:$formatSeconds"
        }
    }
}

