package com.example.mediaplayer.ui.fragments.videoList

import android.Manifest
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.utils.observeOnce
import com.example.mediaplayer.databinding.FragmentVideoListBinding
import com.example.mediaplayer.ui.adapters.VideoListAdapter
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class VideoListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel: VideoListViewModel by viewModels()
    private var _binding: FragmentVideoListBinding? = null

    @Inject
    lateinit var adapter: VideoListAdapter
    private lateinit var rvShimmer: ShimmerRecyclerView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        rvShimmer = binding.videoShimmerRV
        rvShimmer.adapter = adapter
        rvShimmer.layoutManager = LinearLayoutManager(this.context)
        startShimmerRecyclerView()
        viewModel.getVideoList()
        setHasOptionsMenu(true)
        getVideo()
        return binding.root
    }

    private fun getVideo() {
        viewModel.videoList.observeOnce(viewLifecycleOwner, { list ->
            if (list != null) {
                adapter.updateDataList(list.toList())
                disableShimmerRecyclerView()
            }
        })
    }

    private fun searchVideoList(search: String) {
        startShimmerRecyclerView()
        viewModel.searchVideoList(search)
        viewModel.searchedList.observeOnce(viewLifecycleOwner, { list ->
            if (list != null) {
                adapter.updateDataList(list)
                disableShimmerRecyclerView()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.video_list_toolbar_menu, menu)
        val search = menu.findItem(R.id.videoListSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search..."

        setupSearchIcon(searchView)
        setupCustomCloseBtn(searchView)
    }

    private fun setupCustomCloseBtn(searchView:SearchView){
        val searchCloseButton: ImageView? = searchView.findViewById(R.id.search_close_btn)
        val icon=ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
        icon?.setTint(ContextCompat.getColor(requireContext(), R.color.searchIconColor))
        searchCloseButton?.setImageDrawable(icon)
        searchCloseButton?.setOnClickListener {
            if (searchView.query.isNotEmpty()) {
                getVideo()
            }
            //Clear query
            searchView.setQuery("", false);

            //Collapse the action view
            searchView.onActionViewCollapsed();
        }
        searchView.setOnQueryTextListener(this)
    }

    private fun setupSearchIcon(searchView:SearchView){
        val searchIcon =  searchView.findViewById<ImageView>(R.id.search_button)
        val icon=ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
        icon?.setTint(ContextCompat.getColor(requireContext(), R.color.searchIconColor))
        searchIcon.setImageDrawable(icon)
    }

    private fun startShimmerRecyclerView() {
        rvShimmer.showShimmer()
    }

    private fun disableShimmerRecyclerView() {
        rvShimmer.hideShimmer()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchVideoList(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}