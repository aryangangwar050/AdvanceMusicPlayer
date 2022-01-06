package com.example.rockmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rockmusic.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySelectionBinding
    private lateinit var adapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RockMusic)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.saRecyclerView.setHasFixedSize(true)
        binding.saRecyclerView.setItemViewCacheSize(11)
//        PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist.addAll(MainActivity.SongListMA)
        binding.saRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SongAdapter(this,MainActivity.SongListMA, selectionActivity = true)
        binding.saRecyclerView.adapter = adapter



    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.navAdd -> {
//                Toast.makeText(this,"Add clicked",Toast.LENGTH_SHORT).show()
//                val intent = Intent(this,SelectionActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.navRemove->{
//                Toast.makeText(this,"Remove icon clicked..",Toast.LENGTH_SHORT).show()
////                val intent = Intent(this,PlaylistActivity::class.java)
////                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.main,menu)

        menu?.findItem(R.id.navfavourite)?.setVisible(false)
        menu?.findItem(R.id.navAdd)?.setVisible(false)
        menu?.findItem(R.id.navRemove)?.setVisible(false)
        menu?.findItem(R.id.navplaylist)?.setVisible(false)

        // For Search view
        val searchView = menu?.findItem(R.id.navSearch)?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.songListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for(song in MainActivity.SongListMA){
                        if(song.title.lowercase().contains(userInput)){
                            MainActivity.songListSearch.add(song)
                        }
                    }
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.songListSearch)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

}