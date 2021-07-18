package com.example.mediaplayer.data.di

import android.content.Context
import androidx.room.Room
import com.example.mediaplayer.MyApplication
import com.example.mediaplayer.data.db.MediaPlayerDataBase
import com.example.mediaplayer.data.db.dao.PlaylistDao
import com.example.mediaplayer.data.db.dao.VideoDao
import com.example.mediaplayer.data.db.datasource.LocalDataSource
import com.example.mediaplayer.data.providers.AudioProvider
import com.example.mediaplayer.data.providers.VideoProvider
import com.example.mediaplayer.data.repository.Repository
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
    ) = Room.databaseBuilder(
        context,
        MediaPlayerDataBase::class.java,
        "media_database"
    ).build()

    @Singleton
    @Provides
    fun provideVideoDao(database: MediaPlayerDataBase): VideoDao {
        return database.videoDao()
    }

    @Singleton
    @Provides
    fun providePlaylistDao(database: MediaPlayerDataBase): PlaylistDao {
        return database.playlistDao()
    }

    @Singleton
    @Provides
    fun provideVideoProvider(@ApplicationContext context: Context): VideoProvider {
        return VideoProvider(context as MyApplication)
    }

    @Singleton
    @Provides
    fun provideAudioProvider(@ApplicationContext context: Context): AudioProvider {
        return AudioProvider(context as MyApplication)
    }


    @Singleton
    @Provides
    fun provideRepository(
        videoProvider: VideoProvider,
        audioProvider: AudioProvider,
        @ApplicationContext context: Context,
        localDataSource: LocalDataSource
    ): Repository {
        return Repository(videoProvider, audioProvider, context, localDataSource)
    }
}