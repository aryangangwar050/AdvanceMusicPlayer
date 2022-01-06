package com.example.rockmusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass: Application() {

    companion object{
        const val CHANNEL_ID = "channel1"
        const val PREVIOUS = "previous"
        const val NEXT = "next"
        const val PLAY = "play"
        const val EXIT = "exit"


    }

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val mChannel = NotificationChannel(CHANNEL_ID,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
            mChannel.description = ("This is a Important channel for showing song!")
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

        }
    }
}