package com.example.mediaplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import kotlinx.android.synthetic.main.playlist_layout_item.view.*
import javax.inject.Inject


class PlaylistAdapter @Inject constructor(): RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    private val playlist= emptyArray<PlaylistEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater
            .from(parent.context)
            .inflate(R.layout.playlist_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlist[position])
    }

    override fun getItemCount(): Int {
        return playlist.size
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(playlist: PlaylistEntity){
            itemView.playlistTitle.text=playlist.name
        }
    }
}