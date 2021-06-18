package com.example.mediaplayer.ui.activity.player

import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.util.Assertions


class ExoPlayerTrackSelection
    (
    private val trackSelector: DefaultTrackSelector,
    private val params: DefaultTrackSelector.Parameters
) {

    fun changeTrack(groundIndex:Int) {
        val mappedTrackInfo = Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)
        for (i in (0 until mappedTrackInfo.rendererCount)) {
            if (showTabForRenderer(mappedTrackInfo, i)) {
                val trackType = mappedTrackInfo.getRendererType(i)
                val trackGroupArray = mappedTrackInfo.getTrackGroups(i)
                trackSelector.setParameters(
                trackSelector.buildUponParameters()
                    .setSelectionOverride(
                        i,
                        trackGroupArray,
                        DefaultTrackSelector.SelectionOverride(groundIndex, 0)
                    ))
            }
        }

    }

    private fun showTabForRenderer(
        mappedTrackInfo: MappingTrackSelector.MappedTrackInfo,
        rendererIndex: Int
    ): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        return isSupportedTrackType(trackType)
    }

    private fun isSupportedTrackType(trackType: Int): Boolean {
        return when (trackType) {
            C.TRACK_TYPE_TEXT -> true
            else -> false
        }
    }
}