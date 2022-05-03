package com.example.listentomusic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.listentomusic.model.Music
import com.example.listentomusic.view.albumplaylist.AlbumFragment
import com.example.listentomusic.view.artistplaylist.ArtistFragment
import com.example.listentomusic.view.home.HomeFragment
import com.example.listentomusic.view.musicplaylist.MusicFragment
import com.example.listentomusic.view.setting.ColorActionBar
import com.google.android.material.navigation.NavigationView
import com.example.listentomusic.model.Album
import com.example.listentomusic.model.Singer
import com.example.listentomusic.view.musicplaylist.MusicFavoriteFragment
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.util.*
import kotlin.collections.ArrayList

/**
 * class Main Activity
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    var drawerLayout: DrawerLayout? = null
    var toggle: ActionBarDrawerToggle? = null

    var musicList: ArrayList<Music> = arrayListOf() // for list of all music
    var albumList: ArrayList<Album> = arrayListOf() // for list of all album
    var singerList: ArrayList<Singer> = arrayListOf() // for list of all artist
    var musicFavoriteList : ArrayList<Music> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        customNavView()
        getData()
        navigationView?.setNavigationItemSelectedListener(this)
    }

    /**
     * to get permission to get data from storage
     */
    private fun getData() {
        val asyncCallWS = AsyncCallWS();
        asyncCallWS.execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class AsyncCallWS : AsyncTask<Void?, Void?, Void?>() {
        val SOAP_ACTION = ""
        val NAMESPACE = "http://services/"
        val URL = "http://192.168.1.188:8080/MyService/MusicService"
        override fun doInBackground(vararg p0: Void?): Void? {
            getAllMusic()
            getAllAlbum()
            getAllSinger()
            getAllFovariteMusic()
            return null
        }

        private fun getAllFovariteMusic() {
            //code here
        }

        private fun getAllMusic() {
            val METHOD_NAME = "getAllMusic"
            try {
                val request = SoapObject(NAMESPACE, METHOD_NAME)
//                Request.addProperty("id",1)
                val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                soapEnvelope.setOutputSoapObject(request)

                val transport = HttpTransportSE(URL)
                transport.call(SOAP_ACTION, soapEnvelope)
                val results = soapEnvelope.response as Vector<SoapObject>
                for (i in 0 until results.size) {
                    val result = results[i]
                    val id = result.getProperty(1).toString()
                    val image = result.getProperty(2).toString()
                    val link = result.getProperty(3).toString()
                    val title = result.getProperty(5).toString()

                    val albumRaw: SoapObject = result.getProperty(0) as SoapObject
                    val idAlbum = albumRaw.getPrimitivePropertyAsString("id").toInt();
                    val titleAlbum = albumRaw.getPrimitivePropertyAsString("title")
                    val imageAlbum = albumRaw.getPrimitivePropertyAsString("image")
                    val album = Album(idAlbum, titleAlbum, imageAlbum)

                    val singerRaw: SoapObject = result.getProperty(4) as SoapObject
                    val idSinger = singerRaw.getPrimitivePropertyAsString("id").toInt();
                    val nameSinger = singerRaw.getPrimitivePropertyAsString("name")
                    val imageSinger = singerRaw.getPrimitivePropertyAsString("image")
                    val ageSinger = singerRaw.getPrimitivePropertyAsString("age").toInt()
                    val singer = Singer(idSinger, nameSinger, imageSinger, ageSinger)
                    val music = Music(id.toInt(), title, link, image, album, singer);
                    musicList.add(music)
                }
            } catch (ex: Exception) {
                println(ex.stackTraceToString())
            }
        }

        private fun getAllAlbum() {
            val METHOD_NAME = "getAllAlbum"
            try {
                val request = SoapObject(NAMESPACE, METHOD_NAME)
//                Request.addProperty("id",1)
                val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                soapEnvelope.setOutputSoapObject(request)

                val transport = HttpTransportSE(URL)
                transport.call(SOAP_ACTION, soapEnvelope)
                val results = soapEnvelope.response as Vector<SoapObject>
                for (i in 0 until results.size) {
                    val result = results[i]
                    val id = result.getProperty(0).toString()
                    val image = result.getProperty(1).toString()
                    val title = result.getProperty(2).toString()
                    val album = Album(id.toInt(),title, image);
                    albumList.add(album)
                }
            } catch (ex: Exception) {
                println(ex.stackTraceToString())
            }

            val homeFragment = HomeFragment(musicList, albumList)
            supportFragmentManager.beginTransaction().replace(R.id.mainView, homeFragment).commit()
        }

        private fun getAllSinger(){
            val METHOD_NAME = "getAllSinger"
            try {
                val request = SoapObject(NAMESPACE, METHOD_NAME)
//                Request.addProperty("id",1)
                val soapEnvelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                soapEnvelope.setOutputSoapObject(request)

                val transport = HttpTransportSE(URL)
                transport.call(SOAP_ACTION, soapEnvelope)
                val results = soapEnvelope.response as Vector<SoapObject>
                for (i in 0 until results.size) {
                    val result = results[i]
                    val id = result.getProperty(1).toString()
                    val image = result.getProperty(2).toString()
                    val name = result.getProperty(3).toString()
                    val age = result.getProperty(0).toString()

                    val singer = Singer(id.toInt(), name, image,age.toInt());
                    singerList.add(singer)
                }

            } catch (ex: Exception) {
                println(ex.stackTraceToString())
            }
        }
    }




    /**
     * init view
     */
    private fun init() {
        navigationView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawer_layout)
    }

    /**
     * custom side bar
     */
    private fun customNavView() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle!!)
        toggle!!.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * for replace view with fragment by choosing item in side bar
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuPlaylist -> {
                val musicFragment = MusicFragment(musicList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, musicFragment)
                    .commit()
            }
            R.id.menuAlbum -> {
                val albumFragment = AlbumFragment(albumList, musicList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, albumFragment)
                    .commit()
            }
            R.id.menuHome -> {
                val homeFragment = HomeFragment(musicList, albumList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, homeFragment)
                    .commit()
            }
            R.id.menuArtist -> {
                val artistFragment = ArtistFragment(musicList, singerList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, artistFragment)
                    .commit()
            }
            R.id.menuSetting -> {
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, ColorActionBar()).commit()
            }
            R.id.favoriteSong -> {
                val musicFavoriteFragment = MusicFavoriteFragment(musicList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, musicFavoriteFragment).commit()
            }
            R.id.menuLogin -> {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}