package com.example.listentomusic.model

import java.io.Serializable

/**
 * data class Album
 *
 *@param id for Album id
 *@param albumName for Name of album
 *@param artistName for name of artist
 *@param albumImg for path to get album image
 *@param nr_of_song for number of Songs
 */
data class Album(
    var id : Int,
    var title: String,
    var image: String,
) : Serializable