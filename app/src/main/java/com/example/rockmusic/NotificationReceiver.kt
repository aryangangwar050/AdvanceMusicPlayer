package com.example.rockmusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent?) {
        when(p1?.action){
            ApplicationClass.PREVIOUS -> prevNextSongNotification(increment = false, context = p0)
            ApplicationClass.PLAY -> {

                if(MusicPlayerActivity.isPlaying) pauseSong() else playSong()
            }
            ApplicationClass.NEXT -> p0?.let { prevNextSongNotification(increment = true, context = p0) }
            ApplicationClass.EXIT -> {
                exitApplication()

            }
        }
    }

     fun playSong(){
        MusicPlayerActivity.isPlaying = true
        MusicPlayerActivity.musicService!!.mediaPlayer!!.start()
        MusicPlayerActivity.musicService!!.showNotification(R.drawable.icon_pause_notification)
        MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
        Song_Now_Playing.binding.playPauseBtnSNP.setIconResource(R.drawable.icon_pause)
    }
    private fun pauseSong(){
        MusicPlayerActivity.isPlaying = false
        MusicPlayerActivity.musicService!!.mediaPlayer!!.pause()
        MusicPlayerActivity.musicService!!.showNotification(R.drawable.icon_play_notification)
        MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_play)
        Song_Now_Playing.binding.playPauseBtnSNP.setIconResource(R.drawable.icon_play)
    }
    private fun prevNextSongNotification(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        MusicPlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_music_profile).fitCenter())
            .into(MusicPlayerActivity.binding.mpaSongImage)
        MusicPlayerActivity.binding.mpaSongTitle.text = MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].title
        Glide.with(context)
            .load(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
            .into(Song_Now_Playing.binding.songImageSNP)
        Song_Now_Playing.binding.songNameSNP.text = MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].title
        playSong()



    }
}