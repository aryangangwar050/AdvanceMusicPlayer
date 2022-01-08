package com.example.rockmusic

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.rockmusic.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class SettingsActivity : AppCompatActivity() {

    lateinit var binding:ActivitySettingsBinding

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        when(MainActivity.themeIndex){
            0->binding.theme1.setBackgroundColor(R.color.main_color)
            1->binding.theme2.setBackgroundColor(R.color.main_color)
            2->binding.theme3.setBackgroundColor(R.color.main_color)
            3->binding.theme4.setBackgroundColor(R.color.main_color)
            4->binding.theme5.setBackgroundColor(R.color.main_color)

        }

        binding.theme1.setOnClickListener{
            saveThemes(0)
        }
        binding.theme2.setOnClickListener{
            saveThemes(1)
        }
        binding.theme3.setOnClickListener{
            saveThemes(2)
            Log.d("TAG","clicked")
        }
        binding.theme4.setOnClickListener{
            saveThemes(3)
        }
        binding.theme5.setOnClickListener{
            saveThemes(4)
            Log.d("TAG","clicked")
        }
        binding.appVersion.text = "App Version: " + BuildConfig.VERSION_NAME

    }
    @SuppressLint("CommitPrefEdits")
    private fun saveThemes(index:Int){
        if(MainActivity.themeIndex != index){

            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex",index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("If you want to Apply this theme than Restart  it..")
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
}