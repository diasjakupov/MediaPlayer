package com.example.mediaplayer.data.di

import android.content.Context
import com.example.mediaplayer.ui.activity.player.ExoPlayerTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
class PlayerModule {


    @Provides
    @ActivityScoped
    fun provideDefaultTrackSelectorForVideoTrack(@ApplicationContext context: Context): DefaultTrackSelector{
        return DefaultTrackSelector(context)
    }

    @Provides
    @ActivityScoped
    fun provideCustomExoPlayerTrackSelector(trackSelector: DefaultTrackSelector): ExoPlayerTrackSelection{
        return ExoPlayerTrackSelection(trackSelector)
    }
}