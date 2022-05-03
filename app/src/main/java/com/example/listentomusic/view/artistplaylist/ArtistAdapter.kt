package com.example.listentomusic.view.artistplaylist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import com.example.listentomusic.model.Singer

/**
 * class ArtistAdapter
 * @param musicList for list of all music
 * @param singerList for list of artist
 */
class ArtistAdapter(
    private val musicList: ArrayList<Music>,
    private val singerList: ArrayList<Singer>,
    private val listener: OnArtistListItemClick
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artistName = itemView.findViewById<TextView>(R.id.tvArtistName)
        val artistImage = itemView.findViewById<ImageView>(R.id.imgArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.artist_item_cardview, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            artistName.text = singerList[position].name
            Glide.with(itemView).asBitmap()
                .load(singerList[position].image).placeholder(R.mipmap.ic_launcher)
                .into(holder.artistImage)
        }

        /**
         * handle item click
         */
        holder.itemView.setOnClickListener() {
            listener.onArtistListItemClick(getMusicListByArtist(musicList, singerList, position))
        }
    }

    /**
     * use to get list of music by artist
     * @param listSinger list of artist
     * @param listMusic list of all music
     * @param position position of artist you choose in list
     */
    private fun getMusicListByArtist(
        listMusic: ArrayList<Music>,
        listSinger: ArrayList<Singer>,
        position: Int
    ): ArrayList<Music> {
        val list: ArrayList<Music> = arrayListOf()

        for (music in listMusic) {
            if (music.singer.id == listSinger[position].id) {
                list.add(music)
            }
        }
        return list
    }

    override fun getItemCount(): Int {
        return singerList.size
    }

    /**
     * Interface to handle item click event
     */
    interface OnArtistListItemClick {
        fun onArtistListItemClick(list: ArrayList<Music>) {

        }
    }
}