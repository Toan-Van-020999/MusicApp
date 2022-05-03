package com.example.listentomusic.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import com.example.listentomusic.model.Album

class HomeAlbumAdapter(
    private val listMusic: ArrayList<Music>,
    private val listAlbum: ArrayList<Album>,
    val listener: OnHomeAlbumListClick
) : RecyclerView.Adapter<HomeAlbumAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImg: ImageView = itemView.findViewById(R.id.albumHomeImg)
        val albumName: TextView = itemView.findViewById(R.id.albumHomeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_album_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            albumName.text = listAlbum[position].title
            val musicImage = listAlbum[position].image
            Glide.with(itemView).asBitmap().load(musicImage).placeholder(R.mipmap.ic_launcher).into(holder.albumImg)
        }

        holder.itemView.setOnClickListener() {
            listener.onHomeAlbumListClick(
                position,
                getMusicListByAlbum(listMusic, listAlbum, position)
            )
        }
    }

    override fun getItemCount(): Int {
        return listAlbum.size
    }

    /**
     * get list of music by album
     */
    private fun getMusicListByAlbum(
        listMusic: ArrayList<Music>,
        listAlbum: ArrayList<Album>,
        position: Int
    ): ArrayList<Music> {
        val list: ArrayList<Music> = arrayListOf()
        for (music in listMusic) {
            if (music.album.id == listAlbum[position].id) {
                list.add(music)
            }
        }
        return list
    }

    /**
     * Interface handle album list item click ai home screen
     */
    interface OnHomeAlbumListClick {
        fun onHomeAlbumListClick(position: Int, list: ArrayList<Music>) {

        }
    }
}