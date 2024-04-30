package com.geetrajstudios.translator

import android.annotation.SuppressLint
import android.app.Notification.Action
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class AboutActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        window.statusBarColor = getColor(R.color.myblue)

        val insta_btn = findViewById<ImageButton>(R.id.Insta_btn)
        insta_btn.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://www.instagram.com/sarveshkamtekar/"))
            startActivity(intent)
        }

        val linkedin_btn = findViewById<ImageButton>(R.id.linkedin_btn)
        linkedin_btn.setOnClickListener{
            intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://www.linkedin.com/in/sarvesh-kamtekar-009721251/"))
            startActivity(intent)
        }

    }
}