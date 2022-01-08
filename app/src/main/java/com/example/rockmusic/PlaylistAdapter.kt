package com.example.rockmusic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rockmusic.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class PlaylistAdapter(private val context: Context,private var playlistList:ArrayList<Playlist>): RecyclerView.Adapter<PlaylistHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val view = PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false)
        return PlaylistHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.playlistTitle.text = playlistList[position].name
        holder.playlistTitle.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do You really want to delete Playlist?")
                .setPositiveButton("Yes"){dialog,_ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
                    dialog.dismiss()
                    refreshPlaylist()
                }
                .setNegativeButton("No"){dialog,_ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
        holder.root.setOnClickListener{
            val intent = Intent(context,playlistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if(PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[playlistDetails.currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.icon_splash_music).centerCrop())
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
        Log.d("TAG","playlist refreshed...!")

    }

}
class PlaylistHolder(binding:PlaylistViewBinding):RecyclerView.ViewHolder(binding.root){
    val playlistTitle = binding.playlistName
    val image = binding.playlistImage
    val delete = binding.playlistDeleteBtn
    val root = binding.root
}