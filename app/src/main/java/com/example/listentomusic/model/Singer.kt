package com.example.listentomusic.model

import java.io.Serializable

/**
 * Data class Artist
 *
 * @param id for artist id
 * @param artistName for name of artist
 * @param numSong for number of Songs
 */
data class Singer(var id: Int, var name: String, var image: String ,var age : Int) : Serializable