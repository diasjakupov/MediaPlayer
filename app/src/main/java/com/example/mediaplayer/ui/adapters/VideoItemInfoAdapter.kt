package com.example.mediaplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.video.CustomMediaItemFormat
import kotlinx.android.synthetic.main.video_audio_subtitle_track_item.view.*
import javax.inject.Inject

class VideoItemInfoAdapter @Inject constructor() : RecyclerView.Adapter<VideoItemInfoAdapter.ViewHolder>() {
    var audioList = emptyList<CustomMediaItemFormat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.video_audio_subtitle_track_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(audioList[position])
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    fun updateDataList(newList: List<CustomMediaItemFormat>) {
        audioList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(item: CustomMediaItemFormat) {
            itemView.videoExtraData.text = item.format
            itemView.videoAudioLang.text = item.language
        }

    }
}