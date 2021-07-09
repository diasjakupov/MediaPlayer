package com.example.mediaplayer.ui.fragments.videoPlayer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.mediaplayer.R
import com.example.mediaplayer.data.utils.round
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.fragment_video_speed.*


class VideoSpeedFragment : DialogFragment() {
    val viewModel by activityViewModels<PlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView=inflater.inflate(R.layout.fragment_video_speed, container, false)
        dialog!!.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        val params = dialog!!.window!!.attributes;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        params.y=150
        dialog!!.window!!.attributes = params;
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.videoSpeed.observe(viewLifecycleOwner, {
            videoSpeedSlider.value=it

            videoSpeedIndicator.text=it.toDouble().round(2).toString()
        })

        videoSpeedSlider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {

            }

        })

        videoSpeedSlider.addOnChangeListener { _, value, _ ->
            viewModel.videoSpeed.value=value
        }
    }


}