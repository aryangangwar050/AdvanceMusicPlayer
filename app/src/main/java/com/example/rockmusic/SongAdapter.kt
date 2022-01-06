package com.example.rockmusic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rockmusic.databinding.SongViewBinding

class SongAdapter(private val context: Context, var songList:ArrayList<Song>,private var playlistdetails:Boolean = false,private val selectionActivity: Boolean = false): RecyclerView.Adapter<SongAdapter.SongHolder>() {

    class SongHolder(binding:SongViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val songTitle = binding.songTitle
        val songAlbum = binding.songAlbum
        val songDuration = binding.songDuration
        val songImage = binding.svImage
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = SongViewBinding.inflate(LayoutInflater.from(context),parent,false)
        return SongHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        holder.songTitle.text = songList[position].title
        holder.songAlbum.text = songList[position].album
        holder.songDuration.text = formatDuration(songList[position].duration)
        Glide.with(context)
            .load(songList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
            .into(holder.songImage)
        when{
            playlistdetails ->{
                holder.root.setOnClickListener{
                    sendIntent(ref = "PlaylistDetailsAdapter",pos = position)

                }
            }
            selectionActivity ->{
                holder.root.setOnClickListener {
                    if(addSong(songList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.main_color))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                }
            }
            else-> {
                holder.root.setOnClickListener {
                    when{
                        MainActivity.search -> sendIntent(ref = "SongAdapterSearch",pos = position)
                        songList[position].id == MusicPlayerActivity.nowPlayinId ->
                            sendIntent(ref="SongNowPlaying",pos = MusicPlayerActivity.position)
                        else -> sendIntent(ref = "SongAdapter",pos= position)
                    }

            }
            }
        }

    }

     fun addSong(song: Song): Boolean {
        PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    fun updateMusicList(searchList :ArrayList<Song>){
        songList = ArrayList()
        songList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref :String,pos: Int){
        val intent = Intent(context,MusicPlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }

    fun refreshPlaylist(){
        songList = ArrayList()
        songList = PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }


}
