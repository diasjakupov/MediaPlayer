package com.example.mediaplayer.ui.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.models.video.VideoAudioInfo
import com.example.mediaplayer.data.models.video.VideoInfo
import com.example.mediaplayer.data.utils.MediaDiffUtils
import com.example.mediaplayer.data.utils.doAsync
import com.example.mediaplayer.ui.fragments.audioList.AudioListFragmentDirections
import com.example.mediaplayer.ui.fragments.videoList.VideoListFragmentDirections
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.delay
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class AudioListAdapter @Inject constructor(
    @ActivityContext val context: Context
) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    var audioList = arrayListOf<AudioInfo>()

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
        audioList=newList as ArrayList<AudioInfo>
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(private val context: Context,
                     itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(audio: AudioInfo) {
            val image=itemView.findViewById<ImageView>(R.id.audioImage)
            val title=itemView.findViewById<TextView>(R.id.audioTitle)
            val author=itemView.findViewById<TextView>(R.id.audioAuthor)
            val layout=itemView.findViewById<ConstraintLayout>(R.id.audioItem)

            Glide
                .with(context)
                .load(audio.embeddedPicture)
                .error(R.drawable.ic_music)
                .into(image)

            title.doAsync {
                delay(3000)
                it.isSelected=true
            }

            title.text=audio.title
            author.text=audio.author

            layout.setOnClickListener {
                val action=AudioListFragmentDirections.actionAudioListToAudioPlayerActivity(audio)
                val navController = Navigation.findNavController(itemView)
                navController.navigate(action)
            }
        }
    }
}