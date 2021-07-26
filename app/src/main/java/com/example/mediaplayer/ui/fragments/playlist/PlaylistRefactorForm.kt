package com.example.mediaplayer.ui.fragments.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_playlist_refactor_form.view.*

@AndroidEntryPoint
class PlaylistRefactorForm : BottomSheetDialogFragment() {
    val viewModel: PlaylistsViewModel by activityViewModels()
    val args:PlaylistRefactorFormArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView=inflater.inflate(R.layout.fragment_playlist_refactor_form, container, false)

        rootView.playlistformApply.setOnClickListener {
            val text=rootView.playlistNameET.text.toString()
            if(text.isNotEmpty()){
                val newPlaylist=PlaylistEntity(text, args.playlist.id)
                viewModel.updatePlaylistEntity(newPlaylist)
                dismiss()
            }else{
                Toast.makeText(requireContext(), "This field should not be empty", Toast.LENGTH_SHORT).show()
            }

        }

        return rootView
    }




    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

}