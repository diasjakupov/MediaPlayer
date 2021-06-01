package com.example.mediaplayer.data.providers

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mediaplayer.data.models.Video
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton


@ActivityRetainedScoped
class VideoProvider @Inject constructor(
    private val application: Application
) {
    private var selection: String? = null
    private var selectionArgs = emptyArray<String>()
    private var sortedOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    private val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    private fun provideProjection(): Array<String> {
        return arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )
    }

    fun getVideoList(): ArrayList<Video> {
        val listOfVideo = arrayListOf<Video>()
        val query = application.contentResolver.query(
            collection,
            provideProjection(),
            selection,
            selectionArgs,
            sortedOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            try {
                while (cursor.moveToNext()) {
                    val idValue = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        idValue
                    )
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(application.applicationContext, contentUri)
                    val time =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toInt()

                    val title = cursor.getString(titleColumn)
                    val size = cursor.getInt(sizeColumn)
                    listOfVideo.add(Video(contentUri, title, time, size))
                }
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
            }
        }
        return listOfVideo
    }

}


