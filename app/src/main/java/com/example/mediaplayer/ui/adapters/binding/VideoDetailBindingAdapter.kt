package com.example.mediaplayer.ui.adapters.binding

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.mediaplayer.data.models.video.VideoInfo
import org.apache.commons.io.FileUtils
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class VideoDetailBindingAdapter {

    companion object{

        private fun presentDuration(data: Long): String{
            val initialSeconds = TimeUnit.MILLISECONDS.toSeconds(data).toInt()
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
            return "$formatHours$formatMinutes$formatSeconds"
        }

        private fun convertSize(data:Long?): String{
            return (if(data != null ){
                FileUtils.byteCountToDisplaySize(data)
            }else{
                ""
            }).toString()
        }

        @JvmStatic
        @BindingAdapter("applyVideoImage")
        fun applyVideoImage(view:ImageView, video: VideoInfo){
            val retriever=MediaMetadataRetriever()
            retriever.setDataSource(view.context, video.contentUri)
            Glide.with(view.context)
                .load(retriever.frameAtTime)
                .into(view)
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("presentDurationWithUnits")
        fun presentDurationWithUnits(view: TextView, data: Long) {
            view.text = presentDuration(data)
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("presentDurationForAudioInfo")
        fun presentDurationForAudioInfo(view: TextView, data: Long) {
            view.text = "Duration: ${presentDuration(data)}"
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("convertSizeForVideoDetail")
        fun convertSizeForVideoDetail(view:TextView,data:Long?){
            view.text= convertSize(data)
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("convertSizeForAudioInfo")
        fun convertSizeForAudioInfo(view:TextView,data:Long?){
            view.text= "Size: ${convertSize(data)}"
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("presentBitrateValue")
        fun presentBitrateValue(view:TextView,data:String?){
            view.text= "Bitrate: $data bit/sec"
        }

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("setQuality")
        fun setQuality(view:TextView, data:Int){
            view.text="${data}p"
        }
    }
}