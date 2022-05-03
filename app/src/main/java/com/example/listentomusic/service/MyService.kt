package com.example.listentomusic.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.listentomusic.PlayMusicActivity
import com.example.listentomusic.R
import com.example.listentomusic.broadcast.MyBroadcast
import com.example.listentomusic.model.Music
import com.example.listentomusic.utils.Contain
import com.example.listentomusic.utils.Contain.CHANNEL_ID
import com.example.listentomusic.view.bottommusiccontrol.BottomMusicControlFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.util.*


class MyService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val musicBind: IBinder = MusicBinder()
    private var listMusic: ArrayList<Music> = arrayListOf()
    private var songName: String = ""
    private var songArtist: String = ""
    private var position = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        initMusicPlayer()
    }

    private fun initMusicPlayer() {
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK);
    }

    /**
     * This function is used to assign value to listMusic
     */
    fun setListMusic(listMs: ArrayList<Music>) {
        listMusic = listMs
    }

    /**
     * This function is used to set the song position in listMusic
     */
    fun setPositionSong(musicPosition: Int) {
        position = musicPosition
    }

    /**
     * This function is used to play songs
     */
    fun playSong() {
        mediaPlayer.reset()
        val playMusic = listMusic[position]
        songName = listMusic[position].title
        songArtist = listMusic[position].singer.name
        mediaPlayer = MediaPlayer.create(applicationContext, playMusic.link.toUri())
        mediaPlayer.start()

        mediaPlayer.isLooping = true
    }

    /**
     * This function is used to skip to the next song
     */
    fun nextSong() {
        position++
        if (position >= listMusic.size) {
            position = 0
        }
        playSong()
        sendNotification()
    }

    /**
     * This function is used to go back to the previous song
     */
    fun previousSong() {
        position--
        if (position < 0) {
            position = listMusic.size - 1
        }
        playSong()
        sendNotification()
    }

    /**
     * This function returns the total time of the song in milliseconds
     */
    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    /**
     * This function is used to control the song being played according to the user's seekbar change event
     */
    fun seekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

    /**
     * This function is used to update the current position of the seekbar corresponding to the currently playing song
     */
    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    inner class MusicBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return musicBind
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionMusic = intent?.getIntExtra("action_service_music", -1)
        handleActionMusic(actionMusic!!)

        return START_NOT_STICKY
    }


    private var linkImage: Bitmap? = null

    /**
     * This function is used to send notifications
     */
    fun sendNotification(){
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val url = URL(listMusic[position].image)
            linkImage = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            setupNotification()
        }
    }

     private fun setupNotification(){
        val mediaSessionCompat = MediaSessionCompat(this, "toanvan")
        mediaSessionCompat.isActive = true;

        mediaSessionCompat.setMetadata(
            MediaMetadataCompat.Builder().putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                mediaPlayer.duration.toLong()
            ).build()
        )

        mediaSessionCompat.setPlaybackState(
            PlaybackStateCompat.Builder().setState(
                PlaybackStateCompat.STATE_PLAYING,
                mediaPlayer.currentPosition.toLong(),
                0F
            ).setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
        )


        val resultIntent = Intent(this, PlayMusicActivity::class.java);
        resultIntent.putExtra("position", position)
        resultIntent.putExtra("listMusic", listMusic)

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val resultPendingIntent =
            PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.disc)
                .setSubText("ToanVan")
                .setContentTitle(songName)
                .setContentText(songArtist)
                .setLargeIcon(linkImage)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(mediaSessionCompat.sessionToken)
                )
                .setContentIntent(resultPendingIntent)


        if (mediaPlayer.isPlaying) {
            notificationBuilder
                .addAction(
                    R.drawable.prev_icon,
                    "Previous",
                    getPendingIntent(this, Contain.ACTION_PREV)
                )
                .addAction(
                    R.drawable.pause_icon,
                    "Pause",
                    getPendingIntent(this, Contain.ACTION_PAUSE)
                )
                .addAction(
                    R.drawable.next_icon,
                    "Next",
                    getPendingIntent(this, Contain.ACTION_NEXT)
                )
        }else {
            notificationBuilder
                .addAction(
                    R.drawable.prev_icon,
                    "Previous",
                    getPendingIntent(this, Contain.ACTION_PREV)
                )
                .addAction(
                    R.drawable.play_icon,
                    "RESUME",
                    getPendingIntent(this, Contain.ACTION_RESUME)
                )
                .addAction(
                    R.drawable.next_icon,
                    "Next",
                    getPendingIntent(this, Contain.ACTION_NEXT)
                )
        }
        val notification = notificationBuilder.build()
        startForeground(1, notification)
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(this, MyBroadcast::class.java)
        intent.putExtra("action_music", action)

        return PendingIntent.getBroadcast(
            context,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * send action with BottomFragment and PlayActivityMusic according to each received action
     */
    private fun handleActionMusic(action: Int) {
        when (action) {
            Contain.ACTION_PAUSE -> {
                sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_PAUSE)
            }
            Contain.ACTION_RESUME -> {
                sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_RESUME)
            }
            Contain.ACTION_NEXT -> {
                sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_NEXT)
            }
            Contain.ACTION_PREV -> {
                sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_PREV)
            }
        }
    }

    fun resumeMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            sendNotification()
            sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_RESUME)
        }
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            sendNotification()
            sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_PAUSE)
        }
    }

    fun startMusic() {
        mediaPlayer.start()
        sendNotification()
    }

    /**
     * This function is used to skip to the next song when the current song is finished
     */
    fun moveToTheNextSong() {
        mediaPlayer.setOnCompletionListener {
            val sharedPreferencesLoop = getSharedPreferences("checkLoop", MODE_PRIVATE)
            val checkLoop = sharedPreferencesLoop.getBoolean("loop", false)
            val sharedPreferencesRandom = getSharedPreferences("checkRandom", MODE_PRIVATE)
            val checkRandom = sharedPreferencesRandom.getBoolean("random", false)

            if (checkRandom) {
                val random = Random()
                position = random.nextInt(listMusic.size)
                playSong()
                BottomMusicControlFragment.myService.sendNotification()
                sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_RANDOM)
            }
            if (checkLoop) {
                playSong()
            }
            if (!checkLoop && !checkRandom) {
                sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_NEXT)
            }
        }
    }

    /**
     * this function used to send actions based on user click event to bottomFragment and playMusicActivity
     */
    private fun sendActionToBottomMusicFragmentAndPlayMusicActivity(action: Int) {
        val intent = Intent("send_data_to_fragment_and_activity")
        val bundle = Bundle()
        bundle.putInt("action", action)
        bundle.putInt("positionCurrentSong", position)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}