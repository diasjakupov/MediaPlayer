package com.example.mediaplayer.data.providers


import android.app.Application
import android.content.ContentUris
import android.content.IntentSender
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size

import com.example.mediaplayer.data.models.video.VideoInfo

import com.example.mediaplayer.data.utils.compareNumber
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

import javax.inject.Inject


@ActivityRetainedScoped
class VideoProvider @Inject constructor(
    private val application: Application,
) : MediaProvider {
    override val selection: String? = null
    override val selectionArgs = emptyArray<String>()
    override val sortedOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    override val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    override fun provideMediaProjection(): Array<String> {
        return arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
        )
    }

    suspend fun getVideoList() = flow {
        val listOfVideo = arrayListOf<VideoInfo>()
        val query = application.contentResolver.query(
            collection,
            provideMediaProjection(),
            selection,
            selectionArgs,
            sortedOrder
        )
        query?.use { cursor ->
            try {
                val data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {

                    val idValue = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        idValue
                    )
                    val dataValue = cursor.getString(data)

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(application.applicationContext, contentUri)

                    val time =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toLong()

                    val size = cursor.getLong(sizeColumn)


                    val width=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()
                    val height=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()

                    val quality = width?.compareNumber(height) ?: 0

                    listOfVideo.add(
                        VideoInfo(
                            contentUri,
                            title,
                            time,
                            size,
                            quality,
                            dataValue,
                            0
                        )
                    )
                    if (listOfVideo.size == 10) {
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

    override fun deleteMediaByUri(uri: Uri): IntentSender? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.createDeleteRequest(
                    application.contentResolver,
                    listOf(uri)
                ).intentSender
            } else {
                application.contentResolver.delete(uri, null, null)
                null
            }
        } catch (e: SecurityException) {
            Log.e("TAG", "security exception caught")
            null
        }
    }
}


