package com.example.mediaplayer.ui.fragments.videoList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.databinding.FragmentVideoInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_video_info.*


class VideoInfoFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentVideoInfoBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<VideoInfoFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoInfoBinding.inflate(inflater, container, false)
        binding.video = args.video

        binding.videoInformation.setOnClickListener {
            val action =
                VideoInfoFragmentDirections.actionVideoInfoFragmentToVideoDetailInfoActivity(
                    args.video
                )
            findNavController().navigate(action)
        }
        binding.videoDeleteItem.setOnClickListener {
            Toast.makeText(this.context, "Clicked", Toast.LENGTH_SHORT).show()
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