package com.clio.hearon

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var hasNextTrack = false
    private var hasPrevTrack = false

    private val queueReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.clio.hearon.UPDATE_QUEUE") {
                hasNextTrack = intent.getBooleanExtra("hasNext", false)
                hasPrevTrack = intent.getBooleanExtra("hasPrev", false)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        ContextCompat.registerReceiver(
            this,
            queueReceiver,
            IntentFilter("com.clio.hearon.UPDATE_QUEUE"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        val notificationProvider = DefaultMediaNotificationProvider.Builder(this).build()
        notificationProvider.setSmallIcon(R.drawable.logo)
        setMediaNotificationProvider(notificationProvider)

        val forwardingPlayer = object : ForwardingPlayer(player) {
            override fun getAvailableCommands(): Player.Commands {
                val builder = super.getAvailableCommands().buildUpon()

                if (hasNextTrack) {
                    builder.add(Player.COMMAND_SEEK_TO_NEXT)
                } else {
                    builder.remove(Player.COMMAND_SEEK_TO_NEXT)
                }

                if (hasPrevTrack) {
                    builder.add(Player.COMMAND_SEEK_TO_PREVIOUS)
                } else {
                    builder.remove(Player.COMMAND_SEEK_TO_PREVIOUS)
                }

                return builder.build()
            }

            override fun seekToNext() {
                if (hasNextTrack) {
                    sendBroadcast(Intent("com.clio.hearon.NEXT_TRACK").setPackage(packageName))
                }
            }

            override fun seekToPrevious() {
                if (hasPrevTrack) {
                    sendBroadcast(Intent("com.clio.hearon.PREV_TRACK").setPackage(packageName))
                }
            }
        }

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("OPEN_PLAYER", true)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, forwardingPlayer)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player != null) {
            player.pause()
            player.release()
        }
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        unregisterReceiver(queueReceiver)
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}