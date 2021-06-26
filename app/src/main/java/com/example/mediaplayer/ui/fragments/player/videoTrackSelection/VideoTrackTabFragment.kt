package com.example.mediaplayer.ui.fragments.player.videoTrackSelection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.CustomFormatOfTrack
import com.example.mediaplayer.ui.activity.player.ExoPlayerTrackSelection
import com.example.mediaplayer.ui.fragments.player.PlayerViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class VideoTrackTabFragment : Fragment() {
    private var args: ArrayList<CustomFormatOfTrack>? = null
    private val viewModel by activityViewModels<PlayerViewModel>()
    @Inject lateinit var trackSelectorUtil: ExoPlayerTrackSelection
    @Inject lateinit var trackSelector: DefaultTrackSelector
    private lateinit var radioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView=inflater.inflate(R.layout.video_track_section, container, false)
        radioGroup=rootView.findViewById(R.id.trackRadioGroup)
        args= arguments?.getParcelableArrayList("LIST OF FORMATS")

        args?.forEach {custom->
            val radioBtn=RadioButton(requireContext())
            val title=if(custom.label == null){
                Locale(custom.language!!).displayName
            }else{
                "${custom.label} - ${Locale(custom.language!!).displayName}"
            }
            radioBtn.text=title
            radioBtn.id=custom.id!!.toInt()
            val index=custom.groupIndex
            radioBtn.setOnClickListener {
                changeTrack(index, custom)
            }
            radioGroup.addView(radioBtn)
        }

        viewModel.videoLanguage.observe(viewLifecycleOwner, {
            setCheckedBtn(it)
        })
        viewModel.videoSubtitle.observe(viewLifecycleOwner, {
            setCheckedBtn(it)
        })

        return rootView
    }

    private fun setCheckedBtn(format: CustomFormatOfTrack){
        val childCount=radioGroup.childCount
        for(i in (0 until childCount)){
            val child=radioGroup.getChildAt(i)
            if(child.id==format.id!!.toInt()){
                (child as RadioButton).isChecked=true
            }
        }
    }

    private fun changeTrack(groupIndex:Int, customFormat: CustomFormatOfTrack){
        trackSelectorUtil.changeTrack(groupIndex, customFormat.selectedType)
        if(customFormat.selectedType== C.TRACK_TYPE_AUDIO){
            viewModel.videoLanguage.value=customFormat
        }else if(customFormat.selectedType==C.TRACK_TYPE_TEXT){
            viewModel.videoSubtitle.value=customFormat
        }

    }

    companion object{
        fun newInstance(list: ArrayList<CustomFormatOfTrack>): VideoTrackTabFragment{
            return VideoTrackTabFragment().apply {
                arguments=Bundle().apply {
                    putParcelableArrayList("LIST OF FORMATS", list)
                }
            }
        }
    }
}