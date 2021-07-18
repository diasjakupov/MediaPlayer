package com.example.mediaplayer.data.providers

import android.app.Application
import android.content.ContentUris
import android.content.IntentSender
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.mediaplayer.data.models.audio.AudioInfo
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class AudioProvider constructor(
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
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Albums.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
        )
    }

    suspend fun getAudioList() = flow {
        val listOfAudio = arrayListOf<AudioInfo>()
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
                val albumColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID)
                val dataColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (cursor.moveToNext()) {
                    val idValue = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val albumId = cursor.getLong(albumColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        idValue
                    )
                    val dataValue=cursor.getString(dataColumn)
                    val albumUri=ContentUris.withAppendedId(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        albumId
                    )
                    val size = cursor.getLong(sizeColumn)

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(application.applicationContext, contentUri)

                    val time =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            ?.toLong()
                    val author=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    val bitrate=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)

                    listOfAudio.add(AudioInfo(
                        contentUri= contentUri,
                        title=title,
                        duration=time,
                        size=size, author=author,
                        albumUri=albumUri, realPath = dataValue,
                        embeddedPicture= retriever.embeddedPicture,
                        bitrate = bitrate))


                    if (listOfAudio.size == 20) {
                        emit(listOfAudio)
                        listOfAudio.clear()
                    }
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