package com.example.mediaplayer.data.models.audio

import android.net.Uri
import android.os.Parcelable
import androidx.room.PrimaryKey
import com.example.mediaplayer.data.models.CustomMediaInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AudioInfo(
    override val contentUri: Uri,
    override val title: String,
    val duration: Long?,
    val size: Long?,
    val author: String?,
    val realPath: String?,
    val embeddedPicture: ByteArray?,
    var playedDuration: Long?=0,
    val bitrate:String?
):Parcelable, CustomMediaInfo(contentUri, title) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioInfo

        if (contentUri != other.contentUri) return false
        if (title != other.title) return false
        if (duration != other.duration) return false
        if (size != other.size) return false
        if (author != other.author) return false
        if (realPath != other.realPath) return false
        if (!embeddedPicture.contentEquals(other.embeddedPicture)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentUri.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + (size?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (realPath?.hashCode() ?: 0)
        result = 31 * result + embeddedPicture.contentHashCode()
        return result
    }
}