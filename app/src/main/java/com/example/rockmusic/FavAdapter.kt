package com.example.rockmusic

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rockmusic.databinding.FavouriteViewBinding

class FavAdapter(private val context: Context, private val songList: ArrayList<Song>):RecyclerView.Adapter<FavHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavHolder {
        val view = FavouriteViewBinding.inflate(LayoutInflater.from(context),parent,false)
        return FavHolder(view)
    }

    override fun onBindViewHolder(holder:FavHolder, position: Int) {
        holder.songName.text = songList[position].title
        Glide.with(context)
            .load(songList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
            val intent   = Intent(context,MusicPlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavAdapter")
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
       return songList.size
    }
}
class FavHolder(binding : FavouriteViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.fvSongImage
        val songName = binding.fvSongTitle
        val root = binding.root

}