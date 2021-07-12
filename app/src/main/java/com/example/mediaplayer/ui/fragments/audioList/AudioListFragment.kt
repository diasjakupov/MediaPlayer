package com.example.mediaplayer.ui.fragments.audioList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioListBinding
import com.example.mediaplayer.databinding.FragmentVideoListBinding
import com.example.mediaplayer.ui.adapters.AudioListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_audio_list.view.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioListFragment : Fragment() {
    private val viewModel: AudioListViewModel by activityViewModels()
    lateinit var adapter: AudioListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var _binding: FragmentAudioListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding=FragmentAudioListBinding.inflate(inflater, container, false)
        viewModel.getAudioList()
        adapter= AudioListAdapter(requireContext())
        recyclerView=binding.audioShimmerRV
        recyclerView.adapter=adapter
        progressBar=binding.loadingProgressBar
        recyclerView.layoutManager=LinearLayoutManager(this.context)
        showProgressBar()
        getAudio()
        return binding.root
    }

    private fun getAudio(){
        lifecycleScope.launch {
            viewModel.audioList.observe(viewLifecycleOwner, {
                if(it != null){
                    adapter.updateDataList(it)
                    hideProgressBar()
                }
            })
        }
    }

    private fun showProgressBar(){
        progressBar.visibility=View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar.visibility=View.INVISIBLE
    }

}