package com.example.mediaplayer.ui.fragments.videoPlayer.videoTrackSelection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.example.mediaplayer.R
import com.example.mediaplayer.ui.activity.player.video.ExoPlayerTrackSelection
import com.example.mediaplayer.ui.adapters.VideoTrackSectionPagerAdapter
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoTrackSelection : DialogFragment() {
    @Inject lateinit var mTrackSelector: DefaultTrackSelector
    private lateinit var pagerAdapter: VideoTrackSectionPagerAdapter
    @Inject lateinit var exoPlayerTrackSelector: ExoPlayerTrackSelection


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_video_track_selection, container, false)
        pagerAdapter= VideoTrackSectionPagerAdapter(
            initFragments(),
            initTitles(),
            childFragmentManager
        )
        val videoTracksViewPager=rootView.findViewById<ViewPager>(R.id.videoTracksViewPager)
        videoTracksViewPager.adapter=pagerAdapter
        rootView.findViewById<TabLayout>(R.id.videoTracksTabLayout).setupWithViewPager(videoTracksViewPager)
        return rootView
    }


    private fun initFragments(): ArrayList<Fragment>{
        return arrayListOf(
            VideoTrackTabFragment.newInstance(exoPlayerTrackSelector.getTrackFormat(C.TRACK_TYPE_AUDIO)),
            VideoTrackTabFragment.newInstance(exoPlayerTrackSelector.getTrackFormat(C.TRACK_TYPE_TEXT))
            )
    }

    private fun initTitles(): ArrayList<String>{
        return arrayListOf("AudioInfo", "Subtitles")
    }
}