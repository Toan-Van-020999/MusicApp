package com.example.listentomusic

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.os.*
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.listentomusic.databinding.ActivityPlayMusicBinding
import com.example.listentomusic.model.Music
import com.example.listentomusic.utils.Contain
import com.example.listentomusic.view.bottommusiccontrol.BottomMusicControlFragment


class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayMusicBinding
    private var position = 0
    private lateinit var listMusic: ArrayList<Music>
    private var checkReceiveData = true;
    private var sharedPreferencesLoop: SharedPreferences? = null
    private var sharePreferencesRandom: SharedPreferences? = null
    private var editorRandom: SharedPreferences.Editor? = null
    private var editorLoop: SharedPreferences.Editor? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val bundle = p1?.extras
            val actionMusic = bundle?.getInt("action")
            if (bundle?.getInt("positionCurrentSong") != null) {
                position = bundle.getInt("positionCurrentSong")
            }
            handlerActivityControlMusic(actionMusic!!)
        }
    }

    /**
     * This function is used to control music playback according to the received action
     */
    private fun handlerActivityControlMusic(action: Int) {
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

    private fun actionRandom() {
        getIntentMethod()
    }

    private fun actionPrev() {
        position--
        if (position < 0) {
            position = listMusic.size - 1
        }
        getIntentMethod()
        binding.playMusicButtonPause.isInvisible = true
        binding.playMusicButtonPlay.isVisible = true
    }

    private fun actionNext() {
        position++
        if (position >= listMusic.size) {
            position = 0
        }
        getIntentMethod()
        binding.playMusicButtonPause.isInvisible = true
        binding.playMusicButtonPlay.isVisible = true
    }

    private fun actionPause() {
        binding.playMusicButtonPause.isVisible = true
        binding.playMusicButtonPlay.isInvisible = true
    }

    private fun actionResume() {
        binding.playMusicButtonPlay.isVisible = true
        binding.playMusicButtonPause.isInvisible = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("send_data_to_fragment_and_activity"))

        getIntentMethod()

        initSharePreferences()

        checkColorButtonShuffleAndLoop()

        listenerEventClickButtonShuffle()

        listenerEventClickButtonLoop()

        currentSongCompletion()

        handlerUpdateSeekbar()

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        /**
         * handle previous button click
         */
        binding.playMusicButtonPrev.setOnClickListener {
            binding.playMusicButtonPause.isInvisible = true
            binding.playMusicButtonPlay.isVisible = true
            sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_PREV)
            binding.tvSeekbarEnd.text =
                formattedTime(BottomMusicControlFragment.myService.getDuration() / 1000)
        }

        /**
         * handle play button click
         */
        binding.playMusicButtonPlay.setOnClickListener {
            binding.playMusicButtonPlay.isInvisible = true
            binding.playMusicButtonPause.isVisible = true
            sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_PAUSE)
        }

        /**
         * handle next button click
         */
        binding.playMusicButtonNext.setOnClickListener {
            binding.playMusicButtonPause.isInvisible = true
            binding.playMusicButtonPlay.isVisible = true
            sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_NEXT)
            binding.tvSeekbarEnd.text =
                formattedTime(BottomMusicControlFragment.myService.getDuration() / 1000)
        }

        /**
         * handle pause button click
         */
        binding.playMusicButtonPause.setOnClickListener {
            binding.playMusicButtonPause.isInvisible = true
            binding.playMusicButtonPlay.isVisible = true
            sendActionToBottomMusicFragmentAndPlayMusicActivity(Contain.ACTION_RESUME)
        }


        /**
         *
         *  handle heart selected button click
         */
        binding.playMusicButtonHeartSelected.setOnClickListener {
            binding.playMusicButtonHeartSelected.setColorFilter(ContextCompat.getColor(this,
                R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

            //call to sql
        }

        /**
         * handle heart button click
         */
        binding.playMusicButtonHeart.setOnClickListener {
            binding.playMusicButtonHeartSelected.setColorFilter(ContextCompat.getColor(this,
                R.color.Pink), android.graphics.PorterDuff.Mode.SRC_IN);

            //call to sql
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun initSharePreferences() {
        sharedPreferencesLoop = getSharedPreferences("checkLoop", MODE_PRIVATE)
        sharePreferencesRandom = getSharedPreferences("checkRandom", MODE_PRIVATE)
        editorRandom = sharePreferencesRandom?.edit()
        editorLoop = sharedPreferencesLoop?.edit()
    }

    private fun checkColorButtonShuffleAndLoop() {
        if (sharePreferencesRandom!!.getBoolean("random", false)) {
            binding.playMusicButtonShuffle.setColorFilter(Color.rgb(138, 43, 226))
        } else {
            binding.playMusicButtonShuffle.setColorFilter(Color.argb(255, 255, 255, 255))
        }

        if (sharedPreferencesLoop!!.getBoolean("loop", false)) {
            binding.playMusicButtonLoop.setColorFilter(Color.rgb(138, 43, 226))
        } else {
            binding.playMusicButtonLoop.setColorFilter(Color.argb(255, 255, 255, 255))
        }
    }

    /**
     * This func is used to update the corresponding seekbar according to the currently playing song
     */
    private fun handlerUpdateSeekbar() {
        val handlerUpdateSeekBar = Handler()
        handlerUpdateSeekBar.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.seekBar.progress =
                        BottomMusicControlFragment.myService.getCurrentPosition()
                    handlerUpdateSeekBar.postDelayed(this, 1000)
                } catch (e: Exception) {
                    binding.seekBar.progress = 0
                }
            }
        }, 0)
    }

    /**
     * this func used listener Event Click Image Button Loop
     */
    private fun listenerEventClickButtonLoop() {
        binding.playMusicButtonLoop.setOnClickListener {
            if (sharedPreferencesLoop!!.getBoolean("loop", false)) {
                editorLoop!!.putBoolean("loop", false)
                binding.playMusicButtonLoop.setColorFilter(Color.argb(255, 255, 255, 255))
            } else {
                binding.playMusicButtonLoop.setColorFilter(Color.rgb(138, 43, 226))
                editorLoop!!.putBoolean("loop", true)

                editorRandom!!.putBoolean("random", false)
                binding.playMusicButtonShuffle.setColorFilter(Color.argb(255, 255, 255, 255))
            }
            editorRandom!!.apply()
            editorLoop!!.apply()
        }
    }

    /**
     * this func used listener Event Click Image Button Shuffle
     */
    private fun listenerEventClickButtonShuffle() {
        binding.playMusicButtonShuffle.setOnClickListener {
            if (sharePreferencesRandom!!.getBoolean("random", false)) {
                editorRandom!!.putBoolean("random", false)
                binding.playMusicButtonShuffle.setColorFilter(Color.argb(255, 255, 255, 255))
            } else {
                binding.playMusicButtonShuffle.setColorFilter(Color.rgb(138, 43, 226))
                editorRandom!!.putBoolean("random", true)

                editorLoop!!.putBoolean("loop", false)
                binding.playMusicButtonLoop.setColorFilter(Color.argb(255, 255, 255, 255))
            }
            editorRandom!!.apply()
            editorLoop!!.apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun currentSongCompletion() {
        val handlerTimeCurrentSong = Handler()
        handlerTimeCurrentSong.postDelayed(object : Runnable {
            override fun run() {
                BottomMusicControlFragment.myService.moveToTheNextSong()
                handlerTimeCurrentSong.postDelayed(this, 500)
            }
        }, 0)
    }

    /**
     * for get music in intent
     */
    private fun getIntentMethod() {
        if (checkReceiveData) {
            checkReceiveData = false
            position = intent.getIntExtra("position", 0)
            listMusic = intent.getSerializableExtra("listMusic") as ArrayList<Music>
        }
        val receiverCheckStatus = intent.getBooleanExtra("checkStatusButtonPlayOrPause", true)
        if (receiverCheckStatus) {
            binding.playMusicButtonPause.isInvisible = true
            binding.playMusicButtonPlay.isVisible = true
        } else {
            binding.playMusicButtonPlay.isInvisible = true
            binding.playMusicButtonPause.isVisible = true
        }

        binding.playMusicTextViewName.text = listMusic[position].title
        binding.playMusicTextViewArtist.text = listMusic[position].singer.name

        // set text for max duration of seek bar
        binding.tvSeekbarEnd.text =
            formattedTime(BottomMusicControlFragment.myService.getDuration() / 1000)

        //set max seekbar
        binding.seekBar.max = BottomMusicControlFragment.myService.getDuration()

        //listen to the change of seekbar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                BottomMusicControlFragment.myService.seekTo(binding.seekBar.progress)
            }

        })

        // for circle image view
        Glide.with(binding.playMusicImg).load(listMusic[position].image).centerCrop()
            .placeholder(R.drawable.ic_launcher_background).into(binding.playMusicImg)

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
        binding.playMusicImg.startAnimation(animation)
        //track the time of the song playing
        val handlerTimeCurrentSong = Handler()
        handlerTimeCurrentSong.postDelayed(object : Runnable {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                binding.tvSeekbarStart.text =
                    formattedTime(BottomMusicControlFragment.myService.getCurrentPosition() / 1000)
                handlerTimeCurrentSong.postDelayed(this, 500)
            }
        }, 0)
    }

    /**
     * use to format time to seek bar text view
     */
    private fun formattedTime(currentPosition: Int): String {
        var totalOut = ""
        var totalNew = ""
        val second = (currentPosition % 60).toString()
        val minute = (currentPosition / 60).toString()
        totalOut = "$minute:$second"
        totalNew = "$minute:0$second"
        return if (second.length == 1) {
            return totalNew
        } else {
            totalOut
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

