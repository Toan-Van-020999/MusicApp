package com.example.listentomusic.model

import java.io.Serializable

data class User(var id : Int ,var username : String , var password : String) : Serializable{
}