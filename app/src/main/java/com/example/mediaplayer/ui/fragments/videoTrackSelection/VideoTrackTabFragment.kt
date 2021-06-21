package com.example.mediaplayer.ui.fragments.videoTrackSelection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.CustomFormatOfTrack
import com.example.mediaplayer.ui.activity.player.ExoPlayerTrackSelection
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.video_track_item.*
import kotlinx.android.synthetic.main.video_track_section.*
import javax.inject.Inject

@AndroidEntryPoint
class VideoTrackTabFragment : Fragment() {
    private var args: ArrayList<CustomFormatOfTrack>? = null
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
            radioBtn.text=custom.format.label
            val index=custom.groupIndex
            radioBtn.setOnClickListener {
                trackSelectorUtil.changeTrack(index, custom.selectedType)
            }
            radioGroup.addView(radioBtn)
        }

        return rootView
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