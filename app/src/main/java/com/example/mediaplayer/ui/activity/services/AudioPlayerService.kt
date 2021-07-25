package com.example.mediaplayer.ui.activity.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.TaskStackBuilder
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.data.repository.Repository
import com.example.mediaplayer.ui.activity.player.audio.AudioPlayerActivity
import com.example.mediaplayer.ui.activity.player.audio.AudioPlayerActivityArgs
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
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : Service() {
    lateinit var player: SimpleExoPlayer
    private lateinit var defaultDataSource: DefaultDataSourceFactory
    lateinit var trackSelector: DefaultTrackSelector
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private lateinit var mediaSession: MediaSessionCompat
    private val NOTIFICATION_ID = 1

    //data
    @Inject
    lateinit var repository: Repository
    lateinit var audioInfo: AudioInfo
    lateinit var audioList: ArrayList<AudioInfo>
    private var isPlaylist: Boolean = false
    private val mBinder: IBinder = LocalBinder()


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
        //get data from intent
        audioInfo = intent?.getParcelableExtra("AUDIO_INFO")!!

        //check if the intent came from playlist activity
        isPlaylist = intent.getBooleanExtra("IS_PLAYLIST", false)

        //get data from repository
        audioList = if (!isPlaylist) {
            repository.audioList.value!!
        } else {
            (repository.audioPlaylist.value as ArrayList<AudioInfo>?)!!
        }

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
            player.prepare()
            player.playWhenReady = true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "DESTROY SERVICE")
        playerNotificationManager.setPlayer(null)
        player.playWhenReady = false
        player.release()
    }

    private fun getLargeIcon(audio: AudioInfo): Bitmap? {
        return if (audio.embeddedPicture != null) {
            BitmapFactory.decodeByteArray(
                audio.embeddedPicture,
                0,
                audio.embeddedPicture.size
            )
        } else {
            null
        }

    }

    private fun initializeNotificationManager() {
        playerNotificationManager = PlayerNotificationManager.Builder(
            applicationContext,
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
                    Log.e("TAG", "ONGOING $notificationId $ongoing")
                    if (ongoing) {
                        startForeground(notificationId, notification)
                    } else {
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
            .setChannelImportance(NotificationUtil.IMPORTANCE_DEFAULT)
            .build()
    }

    private fun initializeConnector() {
        val connector = MediaSessionConnector(mediaSession)
        connector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(
                player: Player,
                windowIndex: Int
            ): MediaDescriptionCompat {
                repository.setUpPlayedAudio(audioList[player.currentWindowIndex])

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