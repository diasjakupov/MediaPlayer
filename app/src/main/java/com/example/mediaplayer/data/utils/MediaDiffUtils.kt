package com.example.mediaplayer.data.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.mediaplayer.data.models.CustomMediaInfo

class MediaDiffUtils (
    private val oldList: List<CustomMediaInfo>,
    private val newList: List<CustomMediaInfo>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return newList[newItemPosition].contentUri.path == oldList[oldItemPosition].contentUri.path
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].title == oldList[oldItemPosition].title
    }
}