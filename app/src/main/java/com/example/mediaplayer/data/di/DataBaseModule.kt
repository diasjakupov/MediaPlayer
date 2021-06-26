package com.example.mediaplayer.data.di

import android.content.Context
import androidx.room.Room
import com.example.mediaplayer.data.db.MediaPlayerDataBase
import com.example.mediaplayer.data.db.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    )= Room.databaseBuilder(
        context,
        MediaPlayerDataBase::class.java,
        "media_database"
    ).build()

    @Singleton
    @Provides
    fun provideVideoDao(database:MediaPlayerDataBase): VideoDao{
        return database.videoDao()
    }
}