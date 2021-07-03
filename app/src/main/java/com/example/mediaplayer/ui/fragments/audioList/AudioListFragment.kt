package com.example.mediaplayer.ui.fragments.audioList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.ui.adapters.AudioListAdapter
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_audio_list.view.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioListFragment : Fragment() {
    private val viewModel: AudioListViewModel by activityViewModels()
    @Inject lateinit var adapter: AudioListAdapter
    private lateinit var rvShimmer: ShimmerRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView=inflater.inflate(R.layout.fragment_audio_list, container, false)
        viewModel.getAudioList()
        rvShimmer=rootView.audioShimmerRV
        rvShimmer.adapter=adapter
        rvShimmer.layoutManager=LinearLayoutManager(this.context)
        rvShimmer.showShimmer()
        getAudio()
        return rootView
    }

    private fun getAudio(){
        lifecycleScope.launch {
            viewModel.audioList.observe(viewLifecycleOwner, {
                Log.e("TAG", "$it")
                if(it != null){
                    adapter.updateDataList(it)
                    rvShimmer.hideShimmer()
                }
            })
        }
    }

}