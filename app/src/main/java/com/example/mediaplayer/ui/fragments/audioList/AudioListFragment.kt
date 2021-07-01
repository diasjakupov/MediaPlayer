package com.example.mediaplayer.ui.fragments.audioList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.mediaplayer.R


class AudioListFragment : Fragment() {
    private val viewModel: AudioListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewModel.getAudioList()
        viewModel.audioList.observe(viewLifecycleOwner, {
            Log.e("TAG", "${it.size} $it")
        })

        return inflater.inflate(R.layout.fragment_audio_list, container, false)
    }

}