package com.geetrajstudios.translator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SelectorScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        setContentView(R.layout.activity_selector_screen)


        window.statusBarColor = getColor(R.color.myblue)
        val button_to_main_scrn = findViewById<Button>(R.id.go_to_text_trans)

        button_to_main_scrn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btn_to_about = findViewById<Button>(R.id.about_btn)
        btn_to_about.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        val btn_img_to_text = findViewById<Button>(R.id.button_img_to_text)
        btn_img_to_text.setOnClickListener {
            val intent = Intent(this, ImageToTextActivity::class.java)
            startActivity(intent)
        }
    }
}