package com.example.mediaplayer.data.providers

import android.app.Application
import android.content.ContentUris
import android.content.IntentSender
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.room.util.FileUtil
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.Video
import com.example.mediaplayer.data.models.VideoInfo
import com.example.mediaplayer.data.utils.compareNumber
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.flow
import java.io.File
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
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
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
            val data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            try {
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
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()

                    val size = cursor.getLong(sizeColumn)
                    val frame = retriever.frameAtTime
                    val thumbnail = if (frame != null) {
                        frame
                    } else {
                        BitmapFactory.decodeResource(application.resources, R.drawable.ic_error)
                    }
                    val quality = frame?.width?.compareNumber(frame.height) ?: 0

                    listOfVideo.add(
                        Video(
                            contentUri,
                            title,
                            time,
                            thumbnail,
                            size,
                            quality,
                            dataValue
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

    fun deleteVideoByUri(video: VideoInfo): IntentSender? {
        return try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                MediaStore.createDeleteRequest(application.contentResolver, listOf(video.contentUri)).intentSender
            }else {
                application.contentResolver.delete(video.contentUri, null, null)
                null
            }
        }catch (e: SecurityException){
            Log.e("TAG", "security exception caught")
            null
        }
    }


}


