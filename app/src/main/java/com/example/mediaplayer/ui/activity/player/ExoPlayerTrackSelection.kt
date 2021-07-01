package com.example.mediaplayer.ui.activity.player

import com.example.mediaplayer.data.models.video.CustomFormatOfTrack
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Assertions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
                    track.add(CustomFormatOfTrack(format.language, format.label, format.id, trackGroupIndex, selectedType))
                }
            }
        }
        return track
    }

    suspend fun getSelectionOverride(selectedType: Int): CustomFormatOfTrack? {
        return withContext(Dispatchers.IO){
            var trackGroup: TrackGroup? =null
            var override: DefaultTrackSelector.SelectionOverride? =null
            val mappedTrackInfo = Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)
            for (i in (0 until mappedTrackInfo.rendererCount)) {
                val trackType = mappedTrackInfo.getRendererType(i)
                if (trackType == selectedType) {
                    val trackGroupArray = mappedTrackInfo.getTrackGroups(i)
                    override = trackSelector.parameters.getSelectionOverride(i, trackGroupArray)
                    if(override != null){
                        trackGroup=trackGroupArray.get(override.groupIndex)
                    }
                }
            }
            if(trackGroup!=null){
                val format=trackGroup.getFormat(0)
                CustomFormatOfTrack(format.language, format.label, format.id, override?.groupIndex ?: 0, selectedType)
            }else{
                null
            }
        }
    }
}