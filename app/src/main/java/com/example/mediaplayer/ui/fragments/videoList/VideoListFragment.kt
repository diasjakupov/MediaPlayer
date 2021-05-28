package com.example.mediaplayer.ui.fragments.videoList

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentVideoListBinding
import com.example.mediaplayer.ui.adapters.VideoListAdapter
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class VideoListFragment : Fragment() {

    private val viewModel: VideoListViewModel by viewModels()
    private var _binding:FragmentVideoListBinding?=null
    @Inject lateinit var adapter: VideoListAdapter
    private lateinit var rvShimmer: ShimmerRecyclerView
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentVideoListBinding.inflate(inflater, container, false)
        rvShimmer=binding.videoShimmerRV
        rvShimmer.adapter=adapter
        rvShimmer.layoutManager=LinearLayoutManager(this.context)
        startShimmerRecyclerView()
        viewModel.getVideoList()
        setHasOptionsMenu(true)
        viewModel.videoList.observe(viewLifecycleOwner, {
            disableShimmerRecyclerView()
            if(!it.isNullOrEmpty()){
                Log.e("TAG", it.toString())
                adapter.updateDataList(it.toList())
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.video_list_toolbar_menu, menu)

        val search = menu.findItem(R.id.videoListSearch)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
    }

    private fun startShimmerRecyclerView(){
        rvShimmer.showShimmer()
    }

    private fun disableShimmerRecyclerView(){
        rvShimmer.hideShimmer()
    }

}