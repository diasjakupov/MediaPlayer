package com.example.mediaplayer.ui.activity.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LifecycleService
import androidx.media.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.repository.Repository
import com.example.mediaplayer.ui.activity.MainActivity
import com.example.mediaplayer.ui.activity.player.AudioPlayerActivity
import com.example.mediaplayer.ui.activity.player.AudioPlayerActivityArgs
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : Service() {
    lateinit var player: SimpleExoPlayer
    @Inject
    lateinit var repository: Repository
    private lateinit var defaultDataSource: DefaultDataSourceFactory
    lateinit var trackSelector: DefaultTrackSelector
    lateinit var audioInfo: AudioInfo
    lateinit var audioList: ArrayList<AudioInfo>
    private val mBinder: IBinder = LocalBinder()
    private val NOTIFICATION_ID = 1
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        defaultDataSource = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "mediaplayer")
        )

        concatenatingMediaSource = ConcatenatingMediaSource()

        initializeNotificationManager()
        setupMediaSession()
        setUIForPlayer()
        initializeConnector()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioInfo = intent?.getParcelableExtra("AUDIO_INFO")!!
        audioList = repository.audioList.value!!
        if (audioInfo.playedDuration == 0L) {
            concatenatingMediaSource.clear()

            audioList.forEach {
                val item = MediaItem
                    .Builder()
                    .setUri(it.contentUri)
                    .build()

                val source = ProgressiveMediaSource
                    .Factory(defaultDataSource)
                    .createMediaSource(item)

                concatenatingMediaSource.addMediaSource(source)
            }


            player.setMediaSource(concatenatingMediaSource)
            player.seekToDefaultPosition(audioList.indexOf(audioInfo))
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        playerNotificationManager.setPlayer(null)
        player.playWhenReady = false
        player.release()
        super.onDestroy()
    }

    private fun getLargeIcon(audio: AudioInfo): Bitmap? {
        return if (audioInfo.embeddedPicture != null) {
            BitmapFactory.decodeByteArray(
                audio.embeddedPicture,
                0,
                audio.embeddedPicture!!.size
            )
        } else {
            null
        }

    }

    private fun initializeNotificationManager() {
        playerNotificationManager = PlayerNotificationManager.Builder(this,
            NOTIFICATION_ID,
            "MediaPlayerAudio",
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return audioList[player.currentWindowIndex].title
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return TaskStackBuilder.create(this@AudioPlayerService).run {
                        addNextIntentWithParentStack(
                            Intent(
                                this@AudioPlayerService,
                                AudioPlayerActivity::class.java
                            ).apply {
                                val audio = audioList[player.currentWindowIndex]
                                audio.playedDuration = player.currentPosition
                                putExtras(AudioPlayerActivityArgs.Builder(audio).build().toBundle())
                            })
                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                    }
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return audioList[player.currentWindowIndex].author
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return getLargeIcon(audioList[player.currentWindowIndex])
                }
            }).setNotificationListener(
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        startForeground(notificationId, notification)
                        player.prepare()
                        player.playWhenReady = true
                    } // allow notification to be dismissed if player is stopped
                    else {
                        stopForeground(false)
                    }

                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopSelf()
                }
            })
            .setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelImportance(PRIORITY_MAX)
            .build()
    }

    private fun initializeConnector() {
        val connector = MediaSessionConnector(mediaSession)
        connector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(
                player: Player,
                windowIndex: Int
            ): MediaDescriptionCompat {
                return MediaDescriptionCompat.Builder()
                    .setTitle(audioList[player.currentWindowIndex].title)
                    .setDescription(audioList[player.currentWindowIndex].author ?: "")
                    .setIconBitmap(getLargeIcon(audioList[player.currentWindowIndex]))
                    .build()
            }
        })
        connector.setPlayer(player)
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "MEDIA_SESSION")
        mediaSession.isActive = true
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
    }

    private fun setUIForPlayer() {
        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setUsePreviousAction(true)
        playerNotificationManager.setUsePreviousActionInCompactView(true)
        playerNotificationManager.setUseNextAction(true)
        playerNotificationManager.setUseNextActionInCompactView(true)
    }

    inner class LocalBinder : Binder() {
        val service: AudioPlayerService
            get() = this@AudioPlayerService
    }

}