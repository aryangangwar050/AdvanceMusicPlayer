package com.example.rockmusic

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rockmusic.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding:ActivityAboutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aboutText.text ="Developed By : Aryan Gangwar" +
                "\n\nIf You Want to provide Feedback, I will love to hear that."+
                "\n\nContact us@codingchamprock050@gmail.com or 9956095335"
    }

}