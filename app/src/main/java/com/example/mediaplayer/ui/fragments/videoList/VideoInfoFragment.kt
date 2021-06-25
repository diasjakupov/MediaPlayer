package com.example.mediaplayer.ui.fragments.videoList

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.databinding.FragmentVideoInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_video_info.*

@AndroidEntryPoint
class VideoInfoFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentVideoInfoBinding? = null
    private val binding get() = _binding!!
    private val args: VideoInfoFragmentArgs by navArgs()
    private val viewModel: VideoListViewModel by activityViewModels()
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoInfoBinding.inflate(inflater, container, false)
        binding.video = args.video
        intentSenderLauncher=registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            if(it.resultCode == RESULT_OK){
                Toast.makeText(this.context, "File was successfully deleted", Toast.LENGTH_SHORT).show()
                viewModel.updateVideoList(args.video)
                dismiss()
            }else{
                Toast.makeText(this.context, "Failed to delete this file", Toast.LENGTH_SHORT).show()
            }
        }

        binding.videoInformation.setOnClickListener {
            val action =
                VideoInfoFragmentDirections.actionVideoInfoFragmentToVideoDetailInfoActivity(
                    args.video
                )
            findNavController().navigate(action)
        }
        binding.videoDeleteItem.setOnClickListener {
           val intentSender=viewModel.deleteVideoByVideo(args.video)
            intentSender?.let {
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(it).build()
                )
            }
        }
        binding.videoAddToFavorite.setOnClickListener {
            Toast.makeText(this.context, "Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.videoAddToPlaylist.setOnClickListener {
            Toast.makeText(this.context, "Clicked", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}