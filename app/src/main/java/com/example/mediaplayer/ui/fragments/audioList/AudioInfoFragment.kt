package com.example.mediaplayer.ui.fragments.audioList

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAudioInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AudioInfoFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAudioInfoBinding? = null
    private val binding get() = _binding!!
    private val args: AudioInfoFragmentArgs by navArgs()
    private val viewModel: AudioListViewModel by activityViewModels()
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioInfoBinding.inflate(inflater, container, false)
        binding.audio = args.audio
        // Inflate the layout for this fragment

        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    Toast.makeText(
                        this.context,
                        "File was successfully deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.updateAudioList(args.audio)
                    viewModel.deleteAudioFromStorage(args.audio)
                    dismiss()
                } else {
                    Toast.makeText(this.context, "Failed to delete this file", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.audioInformation.setOnClickListener {
            if (binding.audioInfoDetail.visibility == View.VISIBLE) {
                binding.audioInfoDetail.visibility = View.GONE;
            } else {
                binding.audioInfoDetail.visibility = View.VISIBLE;
            }


        }

        binding.audioDeleteItem.setOnClickListener {
            val intentSender = viewModel.deleteAudioFromStorage(args.audio)
            intentSender?.let {
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(it).build()
                )
            }
        }
        return binding.root
    }


    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

}