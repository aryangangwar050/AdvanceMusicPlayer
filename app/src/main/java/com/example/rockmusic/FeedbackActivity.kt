package com.example.rockmusic

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.example.rockmusic.databinding.ActivityFeedbackBinding
import java.lang.Exception
import java.util.*
import javax.mail.Authenticator
import javax.mail.Message.RecipientType
import javax.mail.Message.RecipientType.TO
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedbackActivity : AppCompatActivity() {

    lateinit var binding:ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendFeedbackBtn.setOnClickListener {
            val feedbackMsg =  binding.suggestionFeedback.text.toString() + "\n" + binding.emailOptional.text.toString()
            val subject = binding.faSubject.text.toString()
            val userName = "codingchamprock050@gmail.com"
            val pass = "codingChamp@050"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(feedbackMsg.isNotEmpty() && subject.isNotEmpty() && cm.activeNetworkInfo?.isConnectedOrConnecting == true){
                Thread{
                    try {
                        val properties = Properties()
                        properties["mail.smtp.auth"] = "true"
                        properties["mail.smtp.starttls.enable"] = "true"
                        properties["mail.smtp.host"] = "smtp.gmail.com"
                        properties["mail.smtp.port"] = "587"
                        val session = Session.getInstance(properties,object :Authenticator(){
                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(userName,pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject = subject
                        mail.setText(feedbackMsg)
                        mail.setFrom(InternetAddress(userName))
                        mail.setRecipients(RecipientType.TO,InternetAddress.parse(userName))
                        Transport.send(mail)
                    }catch (e: Exception){Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                        Log.d("TAG","error!")
                    }                }.start()
                Toast.makeText(this,"Thanks for Feedback!!",Toast.LENGTH_SHORT).show()
                finish()

            }else{
                Toast.makeText(this, "Something Went Wrong",Toast.LENGTH_SHORT).show()
            }

        }

    }
}
