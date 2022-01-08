package com.example.rockmusic

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rockmusic.databinding.ActivityFavBinding
import com.example.rockmusic.databinding.ActivityPlaylistBinding
import com.example.rockmusic.databinding.PlaylistDailogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class PlaylistActivity : AppCompatActivity() {

    private lateinit var  binding:ActivityPlaylistBinding
    private lateinit var  adapter: PlaylistAdapter
//    private lateinit var  songPlaylist:ArrayList<String>

    companion object {
        var musicPlaylist :MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.paRecyclerView.setItemViewCacheSize(13)
        binding.paRecyclerView.setHasFixedSize(true)
        binding.paRecyclerView.layoutManager = GridLayoutManager(this,2)
        adapter = PlaylistAdapter(this, playlistList = musicPlaylist.ref)
        binding.paRecyclerView.adapter = adapter

        binding.paAddBtn.setOnClickListener {
            customAlertDialog()
        }

    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.playlist_dailog,binding.root,false)
        val binder = PlaylistDailogBinding.bind(customDialog)

        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist🤘")
            .setPositiveButton("add"){dialog,_ ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if(playlistName != null && createdBy != null){
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlaylist(playlistName.toString(),createdBy.toString())
                        dialog.dismiss()
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExist = false
        for(i in musicPlaylist.ref){
            if(name == i.name){
                playlistExist = true
                break
            }
        }
        if(playlistExist){
            Toast.makeText(this,"Playlist Already exists!",Toast.LENGTH_SHORT).show()
        }else{
            Log.d("TAG","Playlist Created!")
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            Log.d("TAG","${name}")
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            Log.d("TAG","${createdBy}")
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
