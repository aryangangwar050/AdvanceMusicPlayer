package com.example.rockmusic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rockmusic.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import kotlin.system.exitProcess

class playlistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter :SongAdapter
    companion object{
        var currentPlaylistPos :Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RockMusic)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist = checkPlaylist(playlist =PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist )

        currentPlaylistPos = intent.extras?.get("index") as Int
        binding.pdRecyclerView.setHasFixedSize(true)
        binding.pdRecyclerView.setItemViewCacheSize(11)

        binding.pdRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SongAdapter(this,PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,true)
        binding.pdRecyclerView.adapter = adapter
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.pdPlaylistName.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.pdPlaylistName.text = "Total ${adapter.itemCount} Songs.\n\n" +
                " Created On: \n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                " -- ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"

        if(adapter.itemCount>0){
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
                .into(binding.pdPlaylistImage)
        }
        adapter.notifyDataSetChanged()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navAdd -> {
                Toast.makeText(this,"Add clicked",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,SelectionActivity::class.java)

                startActivity(intent)
            }
            R.id.navRemove->{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Remove")
                    .setMessage("Do You Want to Remove all songs From this Playlist?")
                    .setPositiveButton("Yes"){dialog,_ ->

                        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                        adapter.refreshPlaylist()
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
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.main,menu)
        menu?.findItem(R.id.navSearch)?.setVisible(false)
        menu?.findItem(R.id.navfavourite)?.setVisible(false)
        menu?.findItem(R.id.navplaylist)?.setVisible(false)


        return super.onCreateOptionsMenu(menu)
    }

}