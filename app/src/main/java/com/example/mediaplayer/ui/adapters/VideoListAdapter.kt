package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.utils.VideoDiffUtils
import com.example.mediaplayer.ui.fragments.videoList.VideoInfoFragment
import com.example.mediaplayer.ui.fragments.videoList.VideoListFragmentDirections
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.activity_video_detail_info.view.*
import kotlinx.android.synthetic.main.video_item_layout.view.*
import org.apache.commons.io.FileUtils
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class VideoListAdapter @Inject constructor(
    @ActivityContext val context:Context
) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    var videoList = emptyList<Video>()
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

    fun updateDataList(newList: List<Video>) {
        val videoDiffUtil = VideoDiffUtils(videoList, newList)
        val result = DiffUtil.calculateDiff(videoDiffUtil, true)
        videoList = newList
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video) {
            val title=itemView.findViewById<TextView>(R.id.videoTitle)
            val image=itemView.findViewById<ImageView>(R.id.videoThumbnail)
            val dots=itemView.findViewById<ImageView>(R.id.videoItemDots)
            val duration=itemView.findViewById<TextView>(R.id.videoDuration)

            Glide.with(context).load(video.thumbnail).into(image)
            title.text = video.name.toString()

            duration.text = convertDuration(video.duration!!)
            dots.setOnClickListener {
                val videoInfo = VideoInfo(video.uri, video.name, video.duration, video.size, video.quality)
                val action =
                    VideoListFragmentDirections.actionVideoListToVideoInfoFragment(videoInfo)
                val navController=Navigation.findNavController(itemView)
                navController.navigate(action)
            }
        }

        private fun convertDuration(data: Int): String {
            val initialSeconds = TimeUnit.MILLISECONDS.toSeconds(data.toLong()).toInt()
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

