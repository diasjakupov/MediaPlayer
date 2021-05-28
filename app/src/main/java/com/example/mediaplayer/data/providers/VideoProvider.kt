package com.example.mediaplayer.data.providers

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mediaplayer.data.models.Video
import java.lang.Exception
import javax.inject.Inject



class VideoProvider @Inject constructor(
    private val application: Application
) {
    private var selection:String? = null
    private var selectionArgs= emptyArray<String>()
    private var sortedOrder="${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    private val collection=if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

    fun getVideoList(): ArrayList<Video>{
        val listOfVideo= arrayListOf<Video>()
        val query=application.contentResolver.query(
            collection,
            provideProjection(),
            selection,
            selectionArgs,
            sortedOrder
        )
        query?.use { cursor->
            val idColumn=cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            try {
                while (cursor.moveToNext()){
                    val idValue = cursor.getLong(idColumn)
                    val nameValue = cursor.getString(nameColumn)
                    val durationValue = cursor.getInt(durationColumn)
                    val sizeValue = cursor.getInt(sizeColumn)
                    val contentUri=ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        idValue
                    )

                    listOfVideo.add(Video(contentUri, nameValue, durationValue, sizeValue))
                }
            }catch (e:Exception){
                Log.e("TAG", e.toString())
            }
        }
        return listOfVideo
    }

}