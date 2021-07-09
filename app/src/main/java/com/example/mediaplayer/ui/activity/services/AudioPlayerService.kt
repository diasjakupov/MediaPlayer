package com.example.mediaplayer.ui.activity.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.audio.AudioInfo
import com.example.mediaplayer.ui.activity.MainActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class AudioPlayerService: Service() {
    lateinit var player: SimpleExoPlayer
    private lateinit var defaultDataSource: DefaultDataSourceFactory
    lateinit var trackSelector: DefaultTrackSelector
    private val mBinder: IBinder = LocalBinder()
    private val NOTIFICATION_ID=1
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        trackSelector=DefaultTrackSelector(this)
        super.onCreate()
        player=SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        defaultDataSource=DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "mediaplayer")
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val args=intent?.getParcelableExtra<AudioInfo>("AUDIO_INFO")
        val mediaItem = MediaItem
            .Builder()
            .setUri(args!!.contentUri)
            .build()

        val mediaSource = ProgressiveMediaSource
            .Factory(defaultDataSource)
            .createMediaSource(mediaItem)

        playerNotificationManager=PlayerNotificationManager.createWithNotificationChannel(this,
            "MediaPlayerAudio", R.string.playback_channel_name,NOTIFICATION_ID,
            object: PlayerNotificationManager.MediaDescriptionAdapter{
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return args.title!!
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val mIntent=Intent(this@AudioPlayerService, MainActivity::class.java)
                    return PendingIntent.getActivity(this@AudioPlayerService, 0, mIntent, 0)
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return args.author
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return BitmapFactory.decodeByteArray(args.embeddedPicture!!, 0, args.embeddedPicture.size);
                }
            },
            object : PlayerNotificationManager.NotificationListener{
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    stopSelf()
                }
            })




        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setUsePreviousAction(true)
        playerNotificationManager.setUseNextAction(true)
        playerNotificationManager.setUseNextActionInCompactView(true)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.playWhenReady=true

        return START_STICKY
    }

    override fun onDestroy() {
        playerNotificationManager.setPlayer(null)
        player.playWhenReady=false
        player.release()
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        val service: AudioPlayerService
            get() = this@AudioPlayerService
    }

}