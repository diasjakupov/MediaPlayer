package com.example.mediaplayer.ui.adapters.binding

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class VideoListItemBindingAdapter {

    companion object {

        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("convertDuration")
        fun convertDuration(view: TextView, data: Int) {
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
            view.text = "$formatHours:$formatMinutes:$formatSeconds"
        }
    }
}