package com.example.listentomusic.view.bottommusiccontrol

import android.content.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.listentomusic.PlayMusicActivity
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import com.example.listentomusic.service.MyService
import com.example.listentomusic.utils.Contain

/**
 * Class BottomMusicControlFragment for bottom music control
 * param: music for the music is playing
 */
class BottomMusicControlFragment(private var position : Int,var listMusic: ArrayList<Music>) : Fragment() {
    private var bottomTvName: TextView? = null
    private var bottomTvArtist: TextView? = null
    private var bottomImgView: ImageView? = null
    private var bottomPlayBtn: ImageButton? = null
    private var bottomPrevBtn: ImageButton? = null
    private var bottomNextBtn: ImageButton? = null
    private var bottomPauseBtn: ImageButton? = null
    private var playIntent: Intent? = null
    private var checkStatusButtonPlayOrPause = true

    companion object{
        lateinit var myService: MyService
    }

    /**
     * Receiver position and actionMusic
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val bundle = p1?.extras
            val actionMusic = bundle?.getInt("action")
            if (bundle?.getInt("positionCurrentSong") != null){
                position = bundle.getInt("positionCurrentSong")
            }
            handlerFragmentControl(actionMusic!!)
        }
    }

    private fun handlerFragmentControl(action: Int) {
        when (action) {
            Contain.ACTION_RESUME -> {
                actionResume()
            }
            Contain.ACTION_PAUSE -> {
                actionPause()
            }
            Contain.ACTION_NEXT -> {
                actionNext()
            }
            Contain.ACTION_PREV -> {
                actionPrev()
            }
            Contain.ACTION_RANDOM -> {
                actionRandom()
            }
        }
    }

    private fun actionPrev() {
        myService.previousSong()
        position--
        if (position < 0) {
            position = listMusic.size - 1
        }
        init()
    }

    private fun actionNext() {
        myService.nextSong()
        position++
        if (position >= listMusic.size) {
            position = 0
        }
        init()
    }

    private fun actionPause() {
        checkStatusButtonPlayOrPause = false
        bottomPauseBtn?.isVisible = true
        bottomPlayBtn?.isInvisible = true
        myService.pauseMusic()
    }

    private fun actionResume() {
        checkStatusButtonPlayOrPause = true
        bottomPlayBtn?.isVisible = true
        bottomPauseBtn?.isInvisible = true
        myService.resumeMusic()
    }

    private fun actionRandom() {
        init()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_music_player_control, container, false)
    }

    /**
     * this function is used to make connection with service
     */
    private val musicConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder: MyService.MusicBinder = service as MyService.MusicBinder
            myService = binder.getService()
            myService.setListMusic(listMusic)
            val intent = Intent(activity, MyService::class.java)
            activity?.startService(intent)
            myService.setPositionSong(position)
            myService.playSong()
            myService.sendNotification()
            currentSongCompletion()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
        }
    }

    /**
     * this function is used to process when the current song has finished playing
     */
    private fun currentSongCompletion(){
        val handlerTimeCurrentSong = Handler()
        handlerTimeCurrentSong.postDelayed(object : Runnable {
            override fun run() {
                myService.moveToTheNextSong()
                handlerTimeCurrentSong.postDelayed(this, 500)
            }
        }, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomTvName = view.findViewById(R.id.tvNameBottom)
        bottomTvArtist = view.findViewById(R.id.tvArtistBottom)
        bottomImgView = view.findViewById(R.id.bottomImgView)
        bottomPlayBtn = view.findViewById(R.id.bottomPlayButton)
        bottomPrevBtn = view.findViewById(R.id.bottomPrevButton)
        bottomNextBtn = view.findViewById(R.id.bottomNextButton)
        bottomPauseBtn = view.findViewById(R.id.bottomPauseButton)

        init()

        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(receiver, IntentFilter("send_data_to_fragment_and_activity"))

        if (playIntent == null) {
            playIntent = Intent(activity, MyService::class.java)
            activity?.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
        }

        /**
         *
         * handle bottom music control click event to view PlayMusicActivity screen
         */
        view.setOnClickListener {
            sendingIntent()
        }

        /**
         * handle button previous click
         */
        bottomPrevBtn?.setOnClickListener {
            myService.previousSong()
            position--
            if (position < 0) {
                position = listMusic.size - 1
            }
            init()
            checkStatusButtonPlayOrPause = true

        }

        /**
         * handle button play click
         */
        bottomPlayBtn?.setOnClickListener {
            checkStatusButtonPlayOrPause = false
            bottomPlayBtn?.isInvisible = true
            bottomPauseBtn?.isVisible = true
            myService.pauseMusic()
        }

        /**
         * handle button pause click
         */
        bottomPauseBtn?.setOnClickListener {
            checkStatusButtonPlayOrPause = true
            bottomPauseBtn?.isInvisible = true
            bottomPlayBtn?.isVisible = true

            myService.startMusic()

        }
        /**
         * handle button next click
         */
        bottomNextBtn?.setOnClickListener {
            myService.nextSong()
            position++
            if (position >= listMusic.size) {
                position = 0
            }
            init()
            checkStatusButtonPlayOrPause = true
        }
    }

    /**
     * initialize the bottom fragment's view
     */
    private fun init() {
        val img = listMusic[position].image
        Glide.with(this)
            .load(img)
            .into(bottomImgView!!)

        bottomTvName?.text = listMusic[position].title
        bottomTvArtist?.text = listMusic[position].singer.name
        bottomPauseBtn?.isInvisible = true
        bottomPlayBtn?.isVisible = true
        val animation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 6000
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        animation.interpolator = LinearInterpolator()
        bottomImgView?.startAnimation(animation)
    }


    /**
     * for sending intent with music is playing to start Playing music activity
     */
    private fun sendingIntent() {
        val intent = Intent(context, PlayMusicActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("listMusic",listMusic)
        intent.putExtra( "checkStatusButtonPlayOrPause",checkStatusButtonPlayOrPause)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }
}