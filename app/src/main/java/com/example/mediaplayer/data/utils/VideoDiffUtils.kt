package com.example.mediaplayer.data.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.mediaplayer.data.models.Video

class VideoDiffUtils(
    private val oldList: List<Video>,
    private val newList: List<Video>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val res=newList[newItemPosition].contentUri.path==oldList[oldItemPosition].contentUri.path
        return res
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val res=newList[newItemPosition].name==oldList[oldItemPosition].name
        return res
    }
}