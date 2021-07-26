package com.example.mediaplayer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.ui.fragments.playlist.PlaylistFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.playlist_layout_item.view.*
import java.util.*
import javax.inject.Inject



class PlaylistAdapter constructor(
    val onDelete: (playlist: PlaylistEntity)->Unit
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    private var playlists = emptyList<PlaylistEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
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

    fun updateDataSet(newPlaylists: List<PlaylistEntity>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("InflateParams")
        fun bind(playlist: PlaylistEntity){
            itemView.playlistTitle.text = playlist.name.capitalize(Locale.ROOT)

            itemView.playlistSettingsIV.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.inflate(R.menu.playlist_settings_menu)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true)
                }

                popupMenu.setOnMenuItemClickListener { menuItem->
                    when(menuItem.itemId){
                        R.id.playlistDelete->{
                            onDelete(playlist)
                            Snackbar.make(itemView, "Item has been deleted", Snackbar.LENGTH_SHORT).show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.playlistUpdate->{
                            val action=PlaylistFragmentDirections.actionPlaylistsFragmentToPlaylistRefactorForm(playlist)
                            val navController = Navigation.findNavController(itemView)
                            navController.navigate(action)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }

                popupMenu.show()
            }

            itemView.playlistIV.setOnClickListener {
                val action =
                    PlaylistFragmentDirections.actionPlaylistsFragmentToPlaylistAudioFragment(
                        playlist
                    )
                val navController = Navigation.findNavController(itemView)
                navController.navigate(action)
            }
        }
    }
}