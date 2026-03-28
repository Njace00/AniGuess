package com.example.aniguess

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {
    
    private var selectedDifficulty: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val playbtn = findViewById<Button>(R.id.playbtn)
        val easybtn = findViewById<Button>(R.id.easybtn)
        val medbtn = findViewById<Button>(R.id.medbtn)
        val hardbtn = findViewById<Button>(R.id.hardbtn)
        val statsbtn = findViewById<Button>(R.id.statsbtn)

        // Animation for Play Now button
        val pulseAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in).apply {
            duration = 500
            repeatCount = 3
        }

        // Colors
        val normalColor = Color.parseColor("#EE964B")
        val glowColor = Color.parseColor("#FFD166") 

        // PLAY NOW CLICK LISTENER
        playbtn.setOnClickListener {
            if (selectedDifficulty == null) {
                Toast.makeText(this, "Select a difficulty first!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = when (selectedDifficulty) {
                    "EASY" -> Intent(this, EasyModeActivity::class.java)
                    "MEDIUM" -> Intent(this, MediumModeActivity::class.java)
                    "HARD" -> Intent(this, HardModeActivity::class.java)
                    else -> null
                }
                intent?.let { startActivity(it) }
            }
        }

        // DIFFICULTY CLICK LISTENERS
        easybtn.setOnClickListener {
            selectedDifficulty = "EASY"
            playbtn.startAnimation(pulseAnim)
            resetButtons(normalColor, easybtn, medbtn, hardbtn)
            easybtn.backgroundTintList = ColorStateList.valueOf(glowColor)
            easybtn.elevation = 20f
        }

        medbtn.setOnClickListener {
            selectedDifficulty = "MEDIUM"
            playbtn.startAnimation(pulseAnim)
            resetButtons(normalColor, easybtn, medbtn, hardbtn)
            medbtn.backgroundTintList = ColorStateList.valueOf(glowColor)
            medbtn.elevation = 20f
        }

        hardbtn.setOnClickListener {
            selectedDifficulty = "HARD"
            playbtn.startAnimation(pulseAnim)
            resetButtons(normalColor, easybtn, medbtn, hardbtn)
            hardbtn.backgroundTintList = ColorStateList.valueOf(glowColor)
            hardbtn.elevation = 20f
        }

        // GALLERY BUTTON CLICK LISTENER
        statsbtn.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
        
        displayRecords()
    }

    override fun onResume() {
        super.onResume()
        displayRecords()
    }

    private fun displayRecords() {
        val sharedPref = getSharedPreferences("GameRecords", Context.MODE_PRIVATE)
        
        val easyBest = sharedPref.getLong("EasyBestTime", -1L)
        val medBest = sharedPref.getLong("MediumBestTime", -1L)
        val hardBest = sharedPref.getLong("HardBestTime", -1L)

        findViewById<TextView>(R.id.easyRecord).text = if (easyBest == -1L) "Easy Mode: --" else "Easy Mode: ${easyBest}s"
        findViewById<TextView>(R.id.medRecord).text = if (medBest == -1L) "Medium Mode: --" else "Medium Mode: ${medBest}s"
        findViewById<TextView>(R.id.hardRecord).text = if (hardBest == -1L) "Hard Mode: --" else "Hard Mode: ${hardBest}s"
    }

    private fun resetButtons(normalColor: Int, vararg buttons: Button) {
        for (button in buttons) {
            button.backgroundTintList = ColorStateList.valueOf(normalColor)
            button.elevation = 4f
        }
    }
}