package com.example.mediaplayer.ui.fragments.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.mediaplayer.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_playlist_form.*

@AndroidEntryPoint
class PlaylistFormFragment : BottomSheetDialogFragment() {

    private val viewModel: PlaylistsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.playlist.observe(viewLifecycleOwner, {
            it.forEach {entity->
                val chip=Chip(this.context)
                chip.text=entity.name
                playlistPlaylists.addView(chip)
            }
        })

        return inflater.inflate(R.layout.fragment_playlist_form, container, false)
    }



    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}