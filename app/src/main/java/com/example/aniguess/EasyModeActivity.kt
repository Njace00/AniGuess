package com.example.aniguess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EasyModeActivity : AppCompatActivity() {

    data class Level(
        val name: String,
        val images: List<Int>,
        val choices: List<String>
    )

    private lateinit var levels: MutableList<Level>
    private var currentLevelIndex = 0
    private var startTime: Long = 0
    private var levelStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_mode)

        initLevels()
        levels.shuffle()
        startTime = System.currentTimeMillis()
        loadLevel()
        
        findViewById<Button>(R.id.homebtn).setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.statsbtn).setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLevels() {
        levels = mutableListOf(
            Level("Pokemon", listOf(R.drawable.pokemon1, R.drawable.pokemon2, R.drawable.pokemon3, R.drawable.pokemon4), listOf("Pokemon", "Naruto", "One Piece", "Dragon Ball")),
            Level("One Piece", listOf(R.drawable.onepiece1, R.drawable.onepiece2, R.drawable.onepiece3, R.drawable.onepiece4), listOf("Naruto", "Bleach", "One Piece", "Dragon Ball")),
            Level("Naruto", listOf(R.drawable.naruto1, R.drawable.naruto2, R.drawable.naruto3, R.drawable.naruto4), listOf("Dragon Ball", "Naruto", "My Hero Academia", "Attack on Titan")),
            Level("Dragon Ball", listOf(R.drawable.dragonball1, R.drawable.dragonball2, R.drawable.dragonball3, R.drawable.dragonball4), listOf("Bleach", "One Piece", "Naruto", "Dragon Ball"))
        )
    }

    private fun loadLevel() {
        if (currentLevelIndex >= levels.size) {
            saveTimeRecord()
            showCompletionDialog()
            return
        }

        levelStartTime = System.currentTimeMillis()
        val level = levels[currentLevelIndex]
        val shuffledImages = level.images.shuffled()
        val shuffledChoices = level.choices.shuffled()

        updateImages(shuffledImages[0], shuffledImages[1], shuffledImages[2], shuffledImages[3])
        setButtonText(shuffledChoices[0], shuffledChoices[1], shuffledChoices[2], shuffledChoices[3])

        val wrongClickListener = { showWrongAnswerDialog() }

        findViewById<Button>(R.id.choice1).setOnClickListener { if (shuffledChoices[0] == level.name) handleCorrectAnswer() else wrongClickListener() }
        findViewById<Button>(R.id.choice2).setOnClickListener { if (shuffledChoices[1] == level.name) handleCorrectAnswer() else wrongClickListener() }
        findViewById<Button>(R.id.choice3).setOnClickListener { if (shuffledChoices[2] == level.name) handleCorrectAnswer() else wrongClickListener() }
        findViewById<Button>(R.id.choice4).setOnClickListener { if (shuffledChoices[3] == level.name) handleCorrectAnswer() else wrongClickListener() }
    }

    private fun handleCorrectAnswer() {
        val levelTime = (System.currentTimeMillis() - levelStartTime) / 1000
        saveLevelTime(levels[currentLevelIndex].name, levelTime)
        
        showCorrectAnswerDialog()
        Handler(Looper.getMainLooper()).postDelayed({
            currentLevelIndex++
            loadLevel()
        }, 1500)
    }

    private fun saveLevelTime(animeName: String, time: Long) {
        val sharedPref = getSharedPreferences("GalleryPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("LevelTime_$animeName", time)
            apply()
        }
    }

    private fun saveTimeRecord() {
        val endTime = System.currentTimeMillis()
        val totalTime = (endTime - startTime) / 1000

        val sharedPref = getSharedPreferences("GameRecords", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("EasyBestTime", totalTime)
            apply()
        }
    }

    private fun showCorrectAnswerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_correct, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        wlp?.y = 300
        window?.attributes = wlp
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({ if (dialog.isShowing) dialog.dismiss() }, 1500)
    }

    private fun showWrongAnswerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_wrong, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        wlp?.y = 300
        window?.attributes = wlp
        dialogView.findViewById<TextView>(R.id.wrongText).text = "Wrong! Try again."
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({ if (dialog.isShowing) dialog.dismiss() }, 1500)
    }

    private fun showCompletionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_completion, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogView.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            val intent = Intent(this, MediumModeActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btnMainMenu).setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateImages(p1: Int, p2: Int, p3: Int, p4: Int) {
        findViewById<ImageView>(R.id.pic1).setImageResource(p1)
        findViewById<ImageView>(R.id.pic2).setImageResource(p2)
        findViewById<ImageView>(R.id.pic3).setImageResource(p3)
        findViewById<ImageView>(R.id.pic4).setImageResource(p4)
    }

    private fun setButtonText(t1: String, t2: String, t3: String, t4: String) {
        findViewById<Button>(R.id.choice1).text = t1
        findViewById<Button>(R.id.choice2).text = t2
        findViewById<Button>(R.id.choice3).text = t3
        findViewById<Button>(R.id.choice4).text = t4
    }
}