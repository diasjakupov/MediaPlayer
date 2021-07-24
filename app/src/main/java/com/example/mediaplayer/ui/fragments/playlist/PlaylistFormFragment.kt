package com.example.mediaplayer.ui.fragments.playlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_playlist_form.*

@AndroidEntryPoint
class PlaylistFormFragment : BottomSheetDialogFragment() {
    private val viewModel: PlaylistsViewModel by activityViewModels()
    private val args: PlaylistFormFragmentArgs by navArgs()
    private lateinit var playlistET: EditText
    private lateinit var playlistApplyBtn: Button
    private lateinit var playlistChipGroup: ChipGroup
    private var previousList: MutableList<PlaylistEntity> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_playlist_form, container, false)
        var checkedChip=0

        playlistET=rootView.findViewById(R.id.playlistNameET)
        playlistApplyBtn=rootView.findViewById(R.id.playlistformApply)
        playlistChipGroup=rootView.findViewById(R.id.playlistPlaylists)

        viewModel.playlist.observe(viewLifecycleOwner, {
            val changes=it-previousList
            changes.forEach {entity->
                val chip=Chip(this.context)
                chip.text=entity.name
                chip.id=entity.id
                playlistChipGroup.addView(chip)
            }
            previousList= it as MutableList<PlaylistEntity>
        })

        playlistChipGroup.setOnCheckedChangeListener { group, checkedId ->
            checkedChip=checkedId
            playlistApplyBtn.isEnabled=true
            playlistET.text.clear()
        }

        playlistET.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                playlistApplyBtn.isEnabled=true
                playlistChipGroup.clearCheck()
            }
        }

        playlistApplyBtn.setOnClickListener {
            if(checkedChip == 0 || checkedChip == -1){
                val text=playlistET.text.toString()
                if(text.isNotEmpty()){
                    viewModel.createNewPlaylist(text, args.audio)
                    playlistET.text.clear()
                }else{
                    Toast.makeText(this.context, "This field should not be empty", Toast.LENGTH_SHORT).show()
                }
            }else{
                viewModel.createNewAudioEntity(args.audio, checkedChip)
            }
            dismiss()
        }

        return rootView
    }



    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}