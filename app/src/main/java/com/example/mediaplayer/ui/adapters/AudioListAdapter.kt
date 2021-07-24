package com.example.mediaplayer.ui.adapters


import android.content.Context
import android.content.Intent
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections

import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.utils.MediaDiffUtils
import com.example.mediaplayer.data.utils.doAsync
import com.example.mediaplayer.ui.activity.services.AudioPlayerService
import com.example.mediaplayer.ui.fragments.audioList.AudioInfoFragmentDirections
import com.example.mediaplayer.ui.fragments.audioList.AudioListFragmentDirections
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.delay

import java.lang.Exception

open class AudioListAdapter protected constructor(
    private val context: Context,
    private val isPlaylist: Boolean
) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    private var audioList = arrayListOf<AudioInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.audio_item_layout, parent, false
        )
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(audioList[position])
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    fun updateDataList(newList: List<AudioInfo>) {
        val result = DiffUtil.calculateDiff(MediaDiffUtils(audioList, newList))
        audioList = newList as ArrayList<AudioInfo>
        result.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val context: Context,
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById<ImageView>(R.id.audioImage)
        val dots: ImageView = itemView.findViewById<ImageView>(R.id.audioInfoIV)
        val title: TextView = itemView.findViewById<TextView>(R.id.audioTitle)
        val author: TextView = itemView.findViewById<TextView>(R.id.audioAuthor)
        val layout: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.audioItem)

        fun bind(audio: AudioInfo) {
            Glide
                .with(context)
                .load(audio.embeddedPicture)
                .error(R.drawable.ic_music)
                .into(image)

            layout.setOnClickListener {
                it.doAsync {
                    val intent = Intent(context, AudioPlayerService::class.java).apply {
                        putExtra("AUDIO_INFO", audio)
                        putExtra("IS_PLAYLIST", isPlaylist)
                    }
                    Util.startForegroundService(context, intent)
                }
            }

            title.text = audio.title
            author.text = audio.author

            if(isPlaylist){
                bindForPlaylistItem(audio)
            }else{
                bindForGeneralItem(audio)
            }

        }

        private fun bindForPlaylistItem(audio: AudioInfo){
            dots.visibility=View.GONE
        }

        private fun bindForGeneralItem(audio: AudioInfo){

            title.doAsync {
                delay(3000)
                it.isSelected = true
            }

            dots.setOnClickListener {
                val navController = Navigation.findNavController(itemView)
                navController.navigate(AudioListFragmentDirections.actionAudioListToAudioInfoFragment(audio))
            }
        }
    }

    class Builder(val context: Context) {
        private var isPlaylist: Boolean = false

        fun setIsPlaylistFlag(value: Boolean): Builder {
            this.isPlaylist = value
            return this
        }


        fun build(): AudioListAdapter {
            return AudioListAdapter(context = context, isPlaylist)
        }
    }
}