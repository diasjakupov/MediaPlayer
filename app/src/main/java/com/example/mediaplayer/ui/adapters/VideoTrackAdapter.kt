package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.video_track_item.view.*
import javax.inject.Inject

class VideoTrackAdapter @Inject constructor(
    @ActivityContext val context: Context
) : RecyclerView.Adapter<VideoTrackAdapter.ViewHolder>() {
    var trackList= emptyList<Format>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(format:Format){
            val btn=itemView.videoTrackItem
            btn.text=format.label
            btn.setOnClickListener {
                Log.e("TAG", "clicked")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_track_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}