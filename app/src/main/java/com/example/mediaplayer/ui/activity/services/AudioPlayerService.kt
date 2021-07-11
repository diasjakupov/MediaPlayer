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
import androidx.media.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.ui.activity.MainActivity
import com.example.mediaplayer.ui.activity.player.AudioPlayerActivity
import com.example.mediaplayer.ui.activity.player.AudioPlayerActivityArgs
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class AudioPlayerService : Service() {
    lateinit var player: SimpleExoPlayer
    private lateinit var defaultDataSource: DefaultDataSourceFactory
    lateinit var trackSelector: DefaultTrackSelector
    lateinit var args: AudioInfo
    private val mBinder: IBinder = LocalBinder()
    private val NOTIFICATION_ID = 1
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        trackSelector = DefaultTrackSelector(this)
        super.onCreate()
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        defaultDataSource = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "mediaplayer")
        )


        val mediaSession = MediaSessionCompat(this, "MEDIA_SESSION")
        mediaSession.isActive = true

        playerNotificationManager = PlayerNotificationManager.Builder(this,
            NOTIFICATION_ID,
            "MediaPlayerAudio",
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return args.title!!
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return TaskStackBuilder.create(this@AudioPlayerService).run {
                        addNextIntentWithParentStack(Intent(this@AudioPlayerService, AudioPlayerActivity::class.java).apply {
                            val audio=args
                            audio.playedDuration=player.currentPosition
                            putExtras(AudioPlayerActivityArgs.Builder(audio).build().toBundle())
                        })
                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                    }
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return args.author
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return getLargeIcon()
                }
            }).setNotificationListener(
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    Log.e("TAG", "POSTED")
                    if (ongoing) {
                        Log.e("TAG", "ongoing")
                        startForeground(notificationId, notification)
                        player.prepare()
                        player.playWhenReady = true
                    } // allow notification to be dismissed if player is stopped
                    else {
                        Log.e("TAG", "not going")
                        stopForeground(false)
                    }

                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    Log.e("TAG", "Cancelled")
                    stopSelf()
                }
            })
            .setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelImportance(PRIORITY_MAX)
            .build()


        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setUsePreviousAction(true)
        playerNotificationManager.setUseNextAction(true)
        playerNotificationManager.setUseNextActionInCompactView(true)
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)

        val connector = MediaSessionConnector(mediaSession)
        connector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(
                player: Player,
                windowIndex: Int
            ): MediaDescriptionCompat {
                return MediaDescriptionCompat.Builder()
                    .setTitle(args.title!!)
                    .setDescription(args.author ?: "")
                    .setIconBitmap(getLargeIcon())
                    .build()
            }
        })
        connector.setPlayer(player)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        args = intent?.getParcelableExtra("AUDIO_INFO")!!
        val mediaItem = MediaItem
            .Builder()
            .setUri(args.contentUri)
            .build()

        val mediaSource = ProgressiveMediaSource
            .Factory(defaultDataSource)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.e("TAG", "DESTROY SERVICE")
        playerNotificationManager.setPlayer(null)
        player.playWhenReady = false
        player.release()
        super.onDestroy()
    }
    private fun getLargeIcon(): Bitmap?{
        return if(args.embeddedPicture != null){
            BitmapFactory.decodeByteArray(
                args.embeddedPicture,
                0,
                args.embeddedPicture!!.size
            )
        }else{
            null
        }

    }

    inner class LocalBinder : Binder() {
        val service: AudioPlayerService
            get() = this@AudioPlayerService
    }

}