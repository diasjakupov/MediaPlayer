package com.example.mediaplayer.data.providers

import android.app.Application
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.utils.compareNumber
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject


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
            MediaStore.Video.Media.SIZE
        )
    }

    suspend fun getVideoList() = flow {
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
                    val frame = retriever.frameAtTime
                    val thumbnail = if (frame != null) {
                        frame
                    } else {
                        BitmapFactory.decodeResource(application.resources, R.drawable.ic_error)
                    }
                    val quality = frame?.width?.compareNumber(frame.height) ?: 0

                    listOfVideo.add(Video(contentUri, title, time, thumbnail, size, quality))
                    if (listOfVideo.size == 4) {
                        emit(listOfVideo)
                        listOfVideo.clear()
                    }
                }
                emit(listOfVideo)
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
            }
        }
    }


}


