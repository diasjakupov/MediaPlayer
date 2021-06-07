package com.example.mediaplayer.ui.adapters.binding

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.models.VideoInfo
import org.apache.commons.io.FileUtils
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class VideoDetailBindingAdapter {

    companion object{


        @JvmStatic
        @BindingAdapter("applyVideoImage")
        fun applyVideoImage(view:ImageView, video:VideoInfo){
            val retriever=MediaMetadataRetriever()
            retriever.setDataSource(view.context, video.uri)
            Glide.with(view.context)
                .load(retriever.frameAtTime)
                .into(view)
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("presentDurationWithUnits")
        fun presentDuration(view: TextView, data: Int) {
            val initialSeconds = TimeUnit.MILLISECONDS.toSeconds(data.toLong()).toInt()
            val hours = (initialSeconds / 3600)
            val minutes = abs(((hours * 3600 - initialSeconds) / 60))
            val seconds = abs((hours * 3600 + minutes * 60 - initialSeconds))
            val formatMinutes = if (minutes != 0) {
                "${minutes}m"
            } else {
                ""
            }
            val formatHours = if (hours != 0) {
                "${hours}h"
            } else {
                ""
            }
            val formatSeconds = if (seconds != 0) {
                "${seconds}s"
            } else {
                ""
            }
            view.text = "$formatHours$formatMinutes$formatSeconds"
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("convertSize")
        fun convertSize(view:TextView,data:Int?){
            view.text= (if(data != null ){
                FileUtils.byteCountToDisplaySize(data.toLong())
            }else{
                ""
            }).toString()
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("setQuality")
        fun setQuality(view:TextView, data:Int){
            view.text="${data}p"
        }
    }
}