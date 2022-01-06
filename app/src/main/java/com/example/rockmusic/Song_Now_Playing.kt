package com.example.rockmusic

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rockmusic.databinding.FragmentSongNowPlayingBinding

class Song_Now_Playing : Fragment() {
    companion object{
        lateinit var  binding:FragmentSongNowPlayingBinding
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_song__now__playing, container, false)
        binding = FragmentSongNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnSNP.setOnClickListener {
            if(MusicPlayerActivity.isPlaying) {
                pauseMusic()
            }else{
                playMusic()
            }
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(),MusicPlayerActivity::class.java)
            intent.putExtra("index",MusicPlayerActivity.position)
            intent.putExtra("class","SongNowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(MusicPlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.songNameSNP.isSelected = true
            Glide.with(this)
                .load(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].artUri)
                .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
                .into(binding.songImageSNP)
            binding.songNameSNP.text = MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].title
            if(MusicPlayerActivity.isPlaying){
                binding.playPauseBtnSNP.setIconResource(R.drawable.icon_pause)
            }else{
                binding.playPauseBtnSNP.setIconResource(R.drawable.icon_play)
            }
            binding.nextBtnSNP.setOnClickListener {
                setSongPosition(increment = true)
                MusicPlayerActivity.musicService!!.createMediaPlayer()
                Glide.with(this)
                    .load(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].artUri)
                    .apply(RequestOptions().placeholder(R.drawable.icon_music_profile).fitCenter())
                    .into(MusicPlayerActivity.binding.mpaSongImage)
                MusicPlayerActivity.binding.mpaSongTitle.text = MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].title
                MusicPlayerActivity.musicService!!.showNotification(R.drawable.icon_pause_notification)
                playMusic()
                Glide.with(this)
                    .load(MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].artUri)
                    .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
                    .into(binding.songImageSNP)
                binding.songNameSNP.text = MusicPlayerActivity.songListMPA[MusicPlayerActivity.position].title
            }
        }

    }
    private fun playMusic(){
        MusicPlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnSNP.setIconResource(R.drawable.icon_pause)
        MusicPlayerActivity.musicService!!.showNotification(R.drawable.icon_pause_notification)
        MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_pause)
        MusicPlayerActivity.isPlaying = true
    }
    private fun pauseMusic(){
        MusicPlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnSNP.setIconResource(R.drawable.icon_play)
        MusicPlayerActivity.musicService!!.showNotification(R.drawable.icon_play_notification)
        MusicPlayerActivity.binding.mpaPlayPauseBtn.setImageResource(R.drawable.icon_play)
        MusicPlayerActivity.isPlaying = false

    }

}
