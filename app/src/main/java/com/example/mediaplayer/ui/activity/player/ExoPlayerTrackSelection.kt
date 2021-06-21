package com.example.mediaplayer.ui.activity.player

import android.util.Log
import com.example.mediaplayer.data.models.CustomFormatOfTrack
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.util.Assertions


class ExoPlayerTrackSelection
    (
    private val trackSelector: DefaultTrackSelector
) {

    fun changeTrack(index: Int, selectedType: Int) {
        val mappedTrackInfo = Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)
        for(i in (0 until mappedTrackInfo.rendererCount)){
            val trackType=mappedTrackInfo.getRendererType(i)
            if(trackType==selectedType){
                val trackGroupArray = mappedTrackInfo.getTrackGroups(i)
                trackSelector.setParameters(
                    trackSelector.buildUponParameters()
                        .clearSelectionOverride(selectedType, trackGroupArray)
                        .setSelectionOverride(
                            i,
                            trackGroupArray,
                            DefaultTrackSelector.SelectionOverride(index, 0)
                        ))
            }
        }
    }

    fun getTrackFormat(selectedType:Int): ArrayList<CustomFormatOfTrack>{
        val track= arrayListOf<CustomFormatOfTrack>()
        val mappedTrackInfo = Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)
        for(i in (0 until mappedTrackInfo.rendererCount)){
            val trackGroupArray = mappedTrackInfo.getTrackGroups(i)
            val trackType=mappedTrackInfo.getRendererType(i)
            if (selectedType==trackType && trackGroupArray.length != 0){
                for (trackGroupIndex in (0 until trackGroupArray.length)){
                    val trackGroupItem=trackGroupArray.get(trackGroupIndex)
                    val format=trackGroupItem.getFormat(0)
                    track.add(CustomFormatOfTrack(format, trackGroupIndex, selectedType))
                }
            }
        }
        return track
    }


    private fun isSupportedTrackType(trackType: Int): Boolean {
        return when (trackType) {
            C.TRACK_TYPE_AUDIO,C.TRACK_TYPE_TEXT -> true
            else -> false
        }
    }
}