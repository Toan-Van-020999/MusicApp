package com.example.listentomusic.model

import java.io.Serializable

/**
 * data class Music
 *
 *@param name: music name
 *@param title: music title
 *@param link: music link mp3
 *@param image: music image
 */
data class Music(
    val id:Int,
    val title: String,
    val link: String,
    val image: String,
    val album : Album,
    val singer : Singer
) : Serializable
