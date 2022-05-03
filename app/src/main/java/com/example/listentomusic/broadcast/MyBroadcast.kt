package com.example.listentomusic.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.listentomusic.service.MyService

class MyBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionMusic = intent?.getIntExtra("action_music",-1) // receive incoming actions
        val intentService = Intent(context, MyService::class.java)
        intentService.putExtra("action_service_music",actionMusic) //send actions to the service
        context?.startService(intentService)
    }
}
