package com.example.rockmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rockmusic.databinding.ActivityFavBinding
import com.example.rockmusic.databinding.FavouriteViewBinding

class FavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavBinding
    private lateinit var adapter: FavAdapter
    companion object{
        var favouriteSongs : ArrayList<Song> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RockMusic)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        favouriteSongs = checkPlaylist(playlist = favouriteSongs)

        binding.fvRecyclerView.setHasFixedSize(true)
        binding.fvRecyclerView.setItemViewCacheSize(13)
        binding.fvRecyclerView.layoutManager = GridLayoutManager(this,4)
        adapter = FavAdapter(this, favouriteSongs)
        binding.fvRecyclerView.adapter = adapter
    }
}