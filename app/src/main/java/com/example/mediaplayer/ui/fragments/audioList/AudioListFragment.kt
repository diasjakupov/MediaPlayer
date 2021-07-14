package com.example.mediaplayer.ui.fragments.audioList

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioListBinding
import com.example.mediaplayer.ui.adapters.AudioListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_audio_list.view.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AudioListFragment : Fragment(), SearchView.OnQueryTextListener {
    private val viewModel: AudioListViewModel by activityViewModels()
    lateinit var adapter: AudioListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var _binding: FragmentAudioListBinding? = null
    private var isSearching = false
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAudioListBinding.inflate(inflater, container, false)
        viewModel.getAudioList()
        adapter = AudioListAdapter(requireContext())
        recyclerView = binding.audioShimmerRV
        recyclerView.adapter = adapter
        progressBar = binding.loadingProgressBar
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        setHasOptionsMenu(true)
        showProgressBar()
        getAudio()
        return binding.root
    }

    private fun searchAudioList(search: String) {
        viewModel.searchAudioList(search.trim())
        showProgressBar()
        viewModel.searchedList.observe(viewLifecycleOwner, { list ->
            if (list != null) {
                hideProgressBar()
                adapter.updateDataList(list)
            }
        })
    }

    private fun getAudio() {
        lifecycleScope.launch {
            viewModel.audioList.observe(viewLifecycleOwner, {
                if (it != null) {
                    adapter.updateDataList(it)
                    hideProgressBar()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.video_list_toolbar_menu, menu)
        val search = menu.findItem(R.id.mediaListSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search..."

        setupSearchIcon(searchView)
        setupCustomCloseBtn(searchView)
    }

    private fun setupCustomCloseBtn(searchView: SearchView) {
        val searchCloseButton: ImageView? = searchView.findViewById(R.id.search_close_btn)
        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
        icon?.setTint(ContextCompat.getColor(requireContext(), R.color.searchIconColor))
        searchCloseButton?.setImageDrawable(icon)
        searchCloseButton?.setOnClickListener {
            isSearching = false
            if (searchView.query.isNotEmpty()) {
                getAudio()
            }
            //Clear query
            searchView.setQuery("", false)

            //Collapse the action view
            searchView.onActionViewCollapsed()
        }
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchAudioList(query)
            isSearching = true
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun setupSearchIcon(searchView: SearchView) {
        val searchIcon = searchView.findViewById<ImageView>(R.id.search_button)
        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
        icon?.setTint(ContextCompat.getColor(requireContext(), R.color.searchIconColor))
        searchIcon.setImageDrawable(icon)
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

}