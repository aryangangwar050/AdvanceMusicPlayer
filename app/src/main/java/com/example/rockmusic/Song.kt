package com.example.rockmusic

import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Song(
    val id:String,
    val title: String,
    val album:String,
    val duration: Long,
    val path : String,
    val artist :String,
    val artUri : String
)
class Playlist{
    lateinit var  name :String
    lateinit var playlist: ArrayList<Song>
    lateinit var createdBy: String
    lateinit var createdOn :String
}
class MusicPlaylist{
    var ref: ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)) - minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)

    return String.format("%02d:%02d",minutes,seconds)
}

fun getImage(path: String): ByteArray? {
    val retreiver = MediaMetadataRetriever()
    retreiver.setDataSource(path)
    return retreiver.embeddedPicture
}
// To set song position when we click next button when last song is playing or prev button when first song is playing...
fun setSongPosition(increment: Boolean){
    if(!MusicPlayerActivity.repeat){
        if(increment){
            if(MusicPlayerActivity.songListMPA.size-1 == MusicPlayerActivity.position){
                MusicPlayerActivity.position = 0
            }else{
                MusicPlayerActivity.position++
            }
        }else{
            if(0 == MusicPlayerActivity.position){
                MusicPlayerActivity.position = MusicPlayerActivity.songListMPA.size-1
            }else{
                MusicPlayerActivity.position--
            }
        }
    }

}
fun exitApplication(){
    if(MusicPlayerActivity.musicService != null){
        MusicPlayerActivity.musicService!!.stopForeground(true)
        MusicPlayerActivity.musicService!!.mediaPlayer!!.release()
        MusicPlayerActivity.musicService = null
        exitProcess(1)
    }
}
fun favouriteChecker(id :String):Int{
    MusicPlayerActivity.isFav = false
    FavActivity.favouriteSongs.forEachIndexed { index, song ->
        if(id == song.id){
            MusicPlayerActivity.isFav = true
            return index
        }
    }
    return  0
}
fun checkPlaylist(playlist :ArrayList<Song>):ArrayList<Song>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists()){
            playlist.removeAt(index)
            Log.d("TAG","not exist")
        }
        Log.d("TAG"," exist")

    }
    return playlist

}

