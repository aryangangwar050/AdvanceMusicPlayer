package com.example.rockmusic

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rockmusic.databinding.ActivityMainBinding
import com.example.rockmusic.databinding.ActivityMusicPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Exception
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class MusicPlayerActivity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var songListMPA :ArrayList<Song>
        var position :Int = 0
        var isPlaying:Boolean = true
        var musicService:MusicService? = null
        lateinit var binding: ActivityMusicPlayerBinding
        var repeat:Boolean = false
        var min15 : Boolean = false
        var min30 : Boolean = false
        var min45 : Boolean = false
        var min60 : Boolean = false
        var nowPlayinId : String = ""
        var i :Int = 0
        var isFav :Boolean = false
        var fIndex : Int = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RockMusic)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeMusicPlayerLayout()



        // Handle Play Pause Buttons Clicks...
        binding.mpaPlayPauseBtn.setOnClickListener {
            if(isPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }

        // Handle Double clicks(To added in Favs)...
        binding.mpaSongImage.setOnClickListener{
//            Toast.makeText(this,"Clicked!",Toast.LENGTH_SHORT).show()
            i++
            val handler = Handler()
            val runnable = Runnable {
                i = 0
            }
            if(i == 1){
//                Toast.makeText(this," Single Clicked!",Toast.LENGTH_SHORT).show()
                handler.postDelayed(runnable,400)
            }else if(i == 2){
                if(isFav){
                    isFav = false
                    FavActivity.favouriteSongs.removeAt(fIndex)
                    Toast.makeText(this,"Removed From Favourites",Toast.LENGTH_SHORT).show()
                    Log.d("TAG","${fIndex}removed!!!")
                }else{
                    isFav = true
                    FavActivity.favouriteSongs.add(songListMPA[position])
                    Toast.makeText(this,"Added in ❤",Toast.LENGTH_SHORT).show()
                    Log.d("TAG","${fIndex}Added!!!")
                }
                i= 0

            }
        }

        // Handle Next button click...
        binding.mpaNextSong.setOnClickListener {
            nextPrevMusic(increment = true)
        }

        // Handle Previous Button Click...
        binding.mpaPrevSong.setOnClickListener {
            nextPrevMusic(increment = false)
        }

        // Handle Seekbar functionality..
        binding.mpaSeekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    musicService!!.mediaPlayer!!.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?)= Unit

        })

        binding.mpaOptionsSong.setOnClickListener {
            optionDialog()
        }

        // To Repeat song..
        binding.mpaRepeatSong.setOnClickListener {

            if(!repeat){
                repeat = true
                Toast.makeText(this,"Repeat: On",Toast.LENGTH_SHORT).show()
                binding.mpaRepeatSong.setColorFilter(ContextCompat.getColor(this,R.color.red))

            }else{
                repeat = false
                Toast.makeText(this,"Repeat: Off",Toast.LENGTH_SHORT).show()
                binding.mpaRepeatSong.setColorFilter(ContextCompat.getColor(this,R.color.black))
            }
        }


    }

    // To fetch image and song title from main activity to music player activity..
    private fun setMusicPlayerLayout(){

        fIndex = favouriteChecker(songListMPA[position].id)
        Glide.with(this)
            .load(songListMPA[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
            .into(binding.mpaSongImage)

        binding.mpaSongTitle.text = songListMPA[position].title
        binding.mpaSongTitle.isSelected = true

        if(repeat){
            binding.mpaRepeatSong.setColorFilter(ContextCompat.getColor(this,R.color.red))
        }


    }

    // To play song.
    private fun createMediaPlayer(){
        try{
            if(musicService!!.mediaPlayer == null){
                musicService!!.mediaPlayer = MediaPlayer()
            }
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(songListMPA[position].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
            musicService!!.showNotification(R.drawable.icon_pause_notification)
            binding.mpaSongStartTime.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.mpaSongEndTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.mpaSeekbar.progress = 0
            binding.mpaSeekbar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayinId = songListMPA[position].id
        }catch(e: Exception){return}

    }

    private fun initializeMusicPlayerLayout(){
        position = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){

            "PlaylistDetailsAdapter"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songListMPA = ArrayList()
                songListMPA.addAll(PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist)
                setMusicPlayerLayout()
            }
            "FavAdapter" ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songListMPA = ArrayList()
                songListMPA.addAll(FavActivity.favouriteSongs)
                setMusicPlayerLayout()
            }
            "SongNowPlaying" ->{
                setMusicPlayerLayout()
                binding.mpaSongStartTime.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.mpaSongEndTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.mpaSeekbar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.mpaSeekbar.max = musicService!!.mediaPlayer!!.duration
                if(isPlaying){
                    binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
                }else{
                    binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_play)
                }
            }
            "SongAdapterSearch" ->{

                // Starting service..
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songListMPA = ArrayList()
                songListMPA.addAll(MainActivity.songListSearch)
                setMusicPlayerLayout()

            }
            "SongAdapter"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songListMPA = ArrayList()
                songListMPA.addAll(MainActivity.SongListMA)
                setMusicPlayerLayout()

            }
        }
    }

    // To Play music
    private fun playMusic(){
        binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.icon_pause_notification)

    }

    // To pause song
    private fun pauseMusic(){
        binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_play)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.icon_play_notification)

    }

    // To song Previous and Next.
    private fun nextPrevMusic(increment :Boolean){
        if(increment){
            setSongPosition(increment = true)
            createMediaPlayer()
            setMusicPlayerLayout()
        }else{

            setSongPosition(increment = false)
            createMediaPlayer()
            setMusicPlayerLayout()
        }

    }



    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }


    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try{
            setMusicPlayerLayout()
        }catch (e: Exception){return}


    }
    @SuppressLint("CutPasteId")
    private fun optionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.option_menus)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.START
        dialog.show()

        dialog.findViewById<TextView>(R.id.mpaEqualizerBtn)?.setOnClickListener {

            try {
                Toast.makeText(this, "Goto Mobile's Equalizer", Toast.LENGTH_SHORT).show()
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 17)
            } catch (e: Exception) {
                Toast.makeText(this, "Equalizer Feature not supported..", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<TextView>(R.id.mpaTimerBtn)?.setOnClickListener {
            val timer = min15 || min30 || min45 || min60
            if(!timer){
                showTimerDialogSheet()
            }else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("You've already set timer..")
                    .setMessage("Do You Want to stop timer?")
                    .setPositiveButton("Yes"){_,_ ->
                        min15 = false
                        min30 = false
                        min45 = false
                        min60 = false
                        dialog.dismiss()

                    }
                    .setNegativeButton("No"){dialog,_ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        dialog.findViewById<TextView>(R.id.mpaShareBtn)?.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(songListMPA[position].path))
//            val shareTitle  = resources.getString(R.string.about_item_android_id)
            startActivity(Intent.createChooser(shareIntent,"shareTitle"))

        }



    }

    private fun showTimerDialogSheet() {
        val timerDialog = BottomSheetDialog(this)
        timerDialog.setContentView(R.layout.timer_layout)
        timerDialog.show()
        timerDialog.findViewById<LinearLayout>(R.id.minutes15)?.setOnClickListener {
            Toast.makeText(this, "Music will stopped after 15 minutes!", Toast.LENGTH_SHORT).show()
            min15 = true
            Thread{Thread.sleep((15*60000).toLong())
            if(min15){
                exitApplication()
            }}.start()
            timerDialog.dismiss()
        }
        timerDialog.findViewById<LinearLayout>(R.id.minutes30)?.setOnClickListener {
            Toast.makeText(this, "Music will stopped after 30 minutes!", Toast.LENGTH_SHORT).show()
            min30 = true
            Thread{Thread.sleep((30*60000).toLong())
                if(min30){
                    exitApplication()
                }}.start()
            timerDialog.dismiss()
        }
        timerDialog.findViewById<LinearLayout>(R.id.minutes45)?.setOnClickListener {
            Toast.makeText(this, "Music will stopped after 45 minutes!", Toast.LENGTH_SHORT).show()
            min45 = true
            Thread{Thread.sleep((45*60000).toLong())
                if(min45){
                    exitApplication()
                }}.start()
            timerDialog.dismiss()
        }
        timerDialog.findViewById<LinearLayout>(R.id.minutes60)?.setOnClickListener {
            Toast.makeText(this, "Music will stopped after 60 minutes!", Toast.LENGTH_SHORT).show()
            min60 = true
            Thread{Thread.sleep((60*60000).toLong())
                if(min60){
                    exitApplication()
                }}.start()
            timerDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode == RESULT_OK) return
    }



}