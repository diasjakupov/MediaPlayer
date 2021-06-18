package com.example.mediaplayer.ui.fragments.videoTrackSelection

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.ui.adapters.VideoTrackAdapter
import com.example.mediaplayer.ui.adapters.VideoTrackSectionPagerAdapter
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.ui.TrackSelectionView


class VideoTrackSelection : Fragment() {
    private lateinit var mTrackSelector: DefaultTrackSelector
    private lateinit var pagerAdapter: VideoTrackSectionPagerAdapter
    private lateinit var recyclerAdapter: VideoTrackAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_video_track_selection, container, false)

        return rootView
    }


    public fun initFragment(trackSelector:DefaultTrackSelector){
        mTrackSelector=trackSelector
    }
}