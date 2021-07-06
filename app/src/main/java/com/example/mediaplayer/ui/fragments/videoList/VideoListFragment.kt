package com.example.mediaplayer.ui.fragments.videoList

import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.utils.updateWithViewedTime
import com.example.mediaplayer.databinding.FragmentVideoListBinding
import com.example.mediaplayer.ui.adapters.VideoListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VideoListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel: VideoListViewModel by activityViewModels()
    private var _binding: FragmentVideoListBinding? = null

    @Inject
    lateinit var adapter: VideoListAdapter
    private lateinit var recyclerView: RecyclerView
    private val binding get() = _binding!!
    private var isSearching = false
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        recyclerView = binding.videoShimmerRV
        recyclerView.adapter = adapter
        progressBar=binding.loadingProgressBar
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        viewModel.getVideoList()
        setHasOptionsMenu(true)
        showProgressBar()
        getVideo()
        return binding.root
    }

    
    private fun getVideo() {
        lifecycleScope.launch{
            viewModel.videoList.observe(viewLifecycleOwner, {  list ->
                viewModel.viewedVideoList.observe(viewLifecycleOwner, { viewedList->
                    if (list != null && !isSearching) {
                        hideProgressBar()
                        adapter.updateDataList(list.updateWithViewedTime(viewedList))

                    }
                })
            })
        }
    }

    private fun searchVideoList(search: String) {
        viewModel.searchVideoList(search)
        showProgressBar()
        viewModel.searchedList.observe(viewLifecycleOwner, { list ->
            viewModel.viewedVideoList.observe(viewLifecycleOwner, { viewedList->
                if (list != null) {
                    hideProgressBar()
                    adapter.updateDataList(list.updateWithViewedTime(viewedList))
                }
            })
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
            isSearching=false
            if (searchView.query.isNotEmpty()) {
                getVideo()
            }
            //Clear query
            searchView.setQuery("", false)

            //Collapse the action view
            searchView.onActionViewCollapsed()
        }
        searchView.setOnQueryTextListener(this)
    }

    private fun setupSearchIcon(searchView:SearchView){
        val searchIcon =  searchView.findViewById<ImageView>(R.id.search_button)
        val icon=ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
        icon?.setTint(ContextCompat.getColor(requireContext(), R.color.searchIconColor))
        searchIcon.setImageDrawable(icon)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchVideoList(query)
            isSearching=true
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun showProgressBar(){
        progressBar.visibility=View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar.visibility=View.INVISIBLE
    }

}