package com.example.rockmusic

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class MusicService: Service() ,AudioManager.OnAudioFocusChangeListener{

    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var  runnable: Runnable
    lateinit var mediaSession:MediaSession
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        return myBinder
    }

    inner class MyBinder :Binder(){
        fun currentService(): MusicService{
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn :Int){
        val intent = Intent(this,MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this,0,intent,0)

        val intentPrevious = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val previousPendingIntent :PendingIntent = PendingIntent.getBroadcast(this,0,intentPrevious,PendingIntent.FLAG_UPDATE_CURRENT)

        val intentNext = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(this,0,intentNext,PendingIntent.FLAG_UPDATE_CURRENT)

        val intentExit = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent: PendingIntent = PendingIntent.getBroadcast(this,0,intentExit,PendingIntent.FLAG_UPDATE_CURRENT)

        val intentPause = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent: PendingIntent = PendingIntent.getBroadcast(this,0,intentPause,PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = getImage(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].path)

        val image = if(imgArt != null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(this.resources,R.drawable.icon_music_profile)
        }



        val notification = NotificationCompat.Builder(this,ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].title)
            .setContentText(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].album)
            .setSmallIcon(R.drawable.icon_music_profile)
            .setLargeIcon(image)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.icon_previous,"Previous",previousPendingIntent)
            .addAction(playPauseBtn,"Play",playPendingIntent)
            .addAction(R.drawable.icon_next,"Next",nextPendingIntent)
            .addAction(R.drawable.icon_exit_notification,"Exit",exitPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0,1,2))
            .setOnlyAlertOnce(true)
            .build()

        startForeground(13,notification)
    }

    fun createMediaPlayer(){
        if(MusicPlayerActivity.musicService!!.mediaPlayer == null){
            MusicPlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
        }
        MusicPlayerActivity.musicService!!.mediaPlayer!!.reset()
        MusicPlayerActivity.musicService!!.mediaPlayer!!.setDataSource(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].path)
        MusicPlayerActivity.musicService!!.mediaPlayer!!.prepare()
        MusicPlayerActivity.musicService!!.mediaPlayer!!.start()
        MusicPlayerActivity.isPlaying = true
        MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
        MusicPlayerActivity.binding.mpaSongStartTime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
        MusicPlayerActivity.binding.mpaSongEndTime.text = formatDuration(mediaPlayer!!.duration.toLong())
        MusicPlayerActivity.binding.mpaSeekbar.progress = 0
        MusicPlayerActivity.binding.mpaSeekbar.max = mediaPlayer!!.duration
        MusicPlayerActivity.nowPlayinId = MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].id

    }
    fun seekBarSetup(){
        runnable = Runnable {
            MusicPlayerActivity.binding.mpaSongStartTime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            MusicPlayerActivity.binding.mpaSeekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onAudioFocusChange(p0: Int) {
        if(p0 <= 0){
            //Pause Music
            MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_play)
            Song_Now_Playing.binding.playPauseBtnSNP.setIconResource(R.drawable.icon_play)
            MusicPlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.icon_play_notification)
            Log.d("TAG","music will Stopped! ")

        }else{
            //Play music
            MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
            Song_Now_Playing.binding.playPauseBtnSNP.setIconResource(R.drawable.icon_pause)
            MusicPlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.icon_pause_notification)

        }
    }

}