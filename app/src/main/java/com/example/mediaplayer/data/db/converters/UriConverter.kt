package com.example.mediaplayer.data.db.converters

import android.net.Uri
import androidx.room.TypeConverter


class UriConverter {

    @TypeConverter
    fun fromUri(uri:String):Uri{
        return Uri.parse(uri)
    }

    @TypeConverter
    fun toUri(uri:Uri):String{
        return uri.toString()
    }
}