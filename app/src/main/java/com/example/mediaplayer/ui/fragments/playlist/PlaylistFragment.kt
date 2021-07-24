package com.example.mediaplayer.ui.fragments.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.ui.adapters.PlaylistAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistFragment : Fragment() {
    @Inject lateinit var adapter: PlaylistAdapter
    val viewModel: PlaylistsViewModel by activityViewModels()
    private lateinit var playlistRV: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_playlist, container, false)
        playlistRV=rootView.findViewById(R.id.playlistRV)
        playlistRV.adapter=adapter
        playlistRV.layoutManager=GridLayoutManager(this.context, 2)

        viewModel.playlist.observe(viewLifecycleOwner, {
            if(it != null){
                adapter.updateDataSet(it)
            }
        })

        return rootView
    }
}