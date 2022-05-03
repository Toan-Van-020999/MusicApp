package com.example.listentomusic.view.artistdetail

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.model.Music

/**
 * class ArtistDetailAdapter
 * @param musicList to list of music by artist
 * @param listener to handle item click event
 */
class ArtistDetailAdapter(
    private val musicList: ArrayList<Music>,
    private val listener: OnArtistDetailPlaylistItemClick
) :
    RecyclerView.Adapter<ArtistDetailAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvNameMusicItem)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtistMusicItem)
        val img: ImageView = itemView.findViewById(R.id.imgMusicItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_music_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            tvName.text = musicList[position].title
            tvArtist.text = musicList[position].title

            //set up image view with glide
            Glide.with(itemView).asBitmap()
                .load(musicList[position].image).placeholder(R.mipmap.ic_launcher)
                .into(holder.img)
            /**
             * handle item click event
             */
            holder.itemView.setOnClickListener {
                listener.onArtistDetailPlaylistItemClick(position, musicList)
            }
        }
    }


    override fun getItemCount(): Int {
        return musicList.size
    }

    /**
     * Interface to handle Item click event
     */
    interface OnArtistDetailPlaylistItemClick {
        fun onArtistDetailPlaylistItemClick(position: Int, list: ArrayList<Music>) {

        }
    }
}