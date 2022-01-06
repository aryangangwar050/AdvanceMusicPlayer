package com.example.rockmusic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rockmusic.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private  lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var songAdapter: SongAdapter

    companion object{
        lateinit var SongListMA :ArrayList<Song>
        lateinit var songListSearch: ArrayList<Song>
        var search : Boolean = false
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RockMusic)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // To build navigation view....
        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimepermission()){
            initialization()
            // For Retreiving Favourites DATA USING shared preferences..
            FavActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouritesSongs",null)
            val typeToken = object: TypeToken<ArrayList<Song>>(){}.type
            if(jsonString != null){
                val data:ArrayList<Song> = GsonBuilder().create().fromJson(jsonString,typeToken)
                FavActivity.favouriteSongs.addAll(data)
            }
            PlaylistActivity.musicPlaylist = MusicPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist",null)
            if(jsonStringPlaylist != null){
                val dataPlaylist:MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist= dataPlaylist
            }
        }


        // Handle Nav bar clicks..
       binding.navbar.setNavigationItemSelectedListener {
           when(it.itemId){
               R.id.feedback -> Toast.makeText(this,"Click Feedback!",Toast.LENGTH_SHORT).show()
               R.id.setting -> Toast.makeText(this,"Click setting!",Toast.LENGTH_SHORT).show()
               R.id.about -> Toast.makeText(this,"Click about!",Toast.LENGTH_SHORT).show()
               R.id.exit -> {
                   val builder = MaterialAlertDialogBuilder(this)
                   builder.setTitle("Exit")
                       .setMessage("Do You Want to exit from this Application?")
                       .setPositiveButton("Yes"){_,_ ->

                           MusicPlayerActivity.musicService!!.stopForeground(true)
                           MusicPlayerActivity.musicService!!.mediaPlayer!!.release()
                           MusicPlayerActivity.musicService = null
                           exitProcess(1)

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
           true

       }

    }

    @SuppressLint("SetTextI18n")
    private fun initialization() {
        //set music adapter to Recycler view..
        search = false
        binding.maRecyclerView.setHasFixedSize(true)
        binding.maRecyclerView.setItemViewCacheSize(13)
        SongListMA = getAllAudio()
        binding.maRecyclerView.layoutManager = LinearLayoutManager(this)
        songAdapter = SongAdapter(this, SongListMA )
        binding.maRecyclerView.adapter =songAdapter
        binding.maTotalSong.text = "Total Songs: ${songAdapter.itemCount}"
    }

    // to show navigation view....
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        when(item.itemId){
            R.id.navfavourite -> {
                val intent = Intent(this,FavActivity::class.java)
                startActivity(intent)
            }
            R.id.navplaylist->{
                val intent = Intent(this,PlaylistActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.main,menu)
        // Only to handle seach clicks!
        val searchView = menu?.findItem(R.id.navSearch)?.actionView as SearchView
        menu.findItem(R.id.navAdd).setVisible(false)
        menu.findItem(R.id.navRemove).setVisible(false)
        searchView.setOnSearchClickListener{
            if(menu != null){
                menu.findItem(R.id.navfavourite).setVisible(false)
                menu.findItem(R.id.navplaylist).setVisible(false)
            }


        }
        searchView.setOnCloseListener(object: SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                if(menu != null){
                    menu.findItem(R.id.navSearch).setVisible(true)
                    menu.findItem(R.id.navfavourite).setVisible(true)
                    menu.findItem(R.id.navplaylist).setVisible(true)
                }
                invalidateOptionsMenu()


                return false
            }

        })
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                songListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for(song in SongListMA){
                        if(song.title.lowercase().contains(userInput)){
                            songListSearch.add(song)
                        }
                    }
                    search = true
                    songAdapter.updateMusicList(searchList = songListSearch)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("Recycle", "Range")
    private fun getAllAudio():ArrayList<Song>{
        val tempList = ArrayList<Song>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection,null,MediaStore.Audio.Media.DATE_ADDED +" DESC", null)
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri,albumIdC).toString()
                    val music = Song(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }

                } while (cursor.moveToNext())
                cursor.close()
            }

        }
        return tempList

    }

    private fun requestRuntimepermission():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),17)

            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode  == 17){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show()
                initialization()

            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),17)
                Toast.makeText(this,"Songs are not fetching",Toast.LENGTH_SHORT).show()

            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
        if(!MusicPlayerActivity.isPlaying && MusicPlayerActivity.musicService != null){
            MusicPlayerActivity.musicService!!.stopForeground(true)
            MusicPlayerActivity.musicService!!.mediaPlayer!!.release()
            MusicPlayerActivity.musicService = null
            exitProcess(1)
        }

    }

    override fun onResume() {
        super.onResume()
        //For String Favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavActivity.favouriteSongs)
        editor.putString("FavouritesSongs",jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()
    }


}