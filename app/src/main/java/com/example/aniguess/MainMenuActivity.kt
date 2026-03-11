package com.example.aniguess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // EASY BUTTON
        val easybtn = findViewById<Button>(R.id.easybtn)

        easybtn.setOnClickListener {
            val intent = Intent(this, EasyModeActivity::class.java)
            startActivity(intent)
        }
    }
}