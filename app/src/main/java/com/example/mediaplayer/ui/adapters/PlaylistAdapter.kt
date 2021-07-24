package com.example.mediaplayer.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.ui.fragments.playlist.PlaylistFragmentDirections
import kotlinx.android.synthetic.main.playlist_layout_item.view.*
import javax.inject.Inject


class PlaylistAdapter @Inject constructor(): RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    private var playlists= emptyList<PlaylistEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater
            .from(parent.context)
            .inflate(R.layout.playlist_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updateDataSet(newPlaylists: List<PlaylistEntity>){
        playlists = newPlaylists
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(playlist: PlaylistEntity){
            itemView.playlistTitle.text=playlist.name

            itemView.playlistIV.setOnClickListener {
                Log.e("TAG", "adapter: $playlist")
                val action=PlaylistFragmentDirections.actionPlaylistsFragmentToPlaylistAudioFragment(playlist)
                val navController = Navigation.findNavController(itemView)
                navController.navigate(action)
            }
        }
    }
}