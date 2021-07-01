package com.example.mediaplayer.data.providers

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.content.IntentSender
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.mediaplayer.data.models.audio.Audio
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@ActivityRetainedScoped
class AudioProvider @Inject constructor(
    private val application: Application,
): MediaProvider {
    override val selection: String?=null
    override val selectionArgs: Array<String> = emptyArray()
    override val sortedOrder: String = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
    override val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    override fun provideMediaProjection(): Array<String> {
        return arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE
        )
    }

    suspend fun getAudioList() = flow {
        val listOfAudio = arrayListOf<Audio>()
        val query = application.contentResolver.query(
            collection,
            provideMediaProjection(),
            selection,
            selectionArgs,
            sortedOrder
        )

        query?.use { cursor ->
            try {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val idValue = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        idValue
                    )
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(application.applicationContext, contentUri)
                    val time =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toLong()

                    val size = cursor.getLong(sizeColumn)

                    listOfAudio.add(Audio(contentUri, title, time, size))
                }
                emit(listOfAudio)
            } catch (e: Exception) {
                e.printStackTrace()
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