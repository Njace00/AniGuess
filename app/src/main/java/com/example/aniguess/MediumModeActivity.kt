package com.example.aniguess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MediumModeActivity : AppCompatActivity() {

    data class Level(
        val name: String,
        val images: List<Int>,
        val choices: List<String>
    )

    private lateinit var allLevels: List<Level>
    private lateinit var shuffledLevels: MutableList<Level>
    private var currentLevelIndex = 0
    private var lives = 3
    private var heartsLost = 0
    private lateinit var hearts: Array<ImageView>
    private var startTime: Long = 0
    private var levelStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medium_mode)

        hearts = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        initLevels()
        shuffledLevels = allLevels.shuffled().toMutableList()
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
        allLevels = listOf(
            Level("Fullmetal Alchemist", listOf(R.drawable.fullmetal1, R.drawable.fullmetal2, R.drawable.fullmetal3, R.drawable.fullmetal4), listOf("Fullmetal Alchemist", "Bleach", "Attack on Titan", "Hunter x Hunter")),
            Level("Bleach", listOf(R.drawable.bleach1, R.drawable.bleach2, R.drawable.bleach3, R.drawable.bleach4), listOf("Naruto", "Death Note", "Bleach", "Jujutsu Kaisen")),
            Level("Attack on Titan", listOf(R.drawable.attackontitan1, R.drawable.attackontitan2, R.drawable.attackontitan3, R.drawable.attackontitan4), listOf("Attack on Titan", "Demon Slayer", "My Hero Academia", "Tokyo Ghoul")),
            Level("Hunter x Hunter", listOf(R.drawable.hunterxhunter1, R.drawable.hunterxhunter2, R.drawable.hunterxhunter3, R.drawable.hunterxhunter4), listOf("One Piece", "Hunter x Hunter", "Fairy Tail", "Black Clover"))
        )
    }

    private fun loadLevel() {
        if (currentLevelIndex >= shuffledLevels.size) {
            saveTimeRecord()
            showCompletionDialog()
            return
        }

        levelStartTime = System.currentTimeMillis()
        val level = shuffledLevels[currentLevelIndex]
        val displayImages = level.images.shuffled()
        val displayChoices = level.choices.shuffled()

        updateImages(displayImages[0], displayImages[1], displayImages[2], displayImages[3])
        setButtonText(displayChoices[0], displayChoices[1], displayChoices[2], displayChoices[3])

        val wrongClickListener = { handleWrongAnswer() }

        findViewById<Button>(R.id.choice1).setOnClickListener { if (displayChoices[0] == level.name) handleCorrectAnswer() else wrongClickListener() }
        findViewById<Button>(R.id.choice2).setOnClickListener { if (displayChoices[1] == level.name) handleCorrectAnswer() else wrongClickListener() }
        findViewById<Button>(R.id.choice3).setOnClickListener { if (displayChoices[2] == level.name) handleCorrectAnswer() else wrongClickListener() }
        findViewById<Button>(R.id.choice4).setOnClickListener { if (displayChoices[3] == level.name) handleCorrectAnswer() else wrongClickListener() }
    }

    private fun handleCorrectAnswer() {
        val levelTime = (System.currentTimeMillis() - levelStartTime) / 1000
        saveLevelTime(shuffledLevels[currentLevelIndex].name, levelTime)
        
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
            putLong("MediumBestTime", totalTime)
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

    private fun handleWrongAnswer() {
        if (heartsLost < 3) {
            hearts[heartsLost].visibility = View.INVISIBLE
            heartsLost++
            lives--
            showWrongAnswerDialog(lives)
        }

        if (lives <= 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                showRetryDialog()
            }, 1500)
        }
    }

    private fun showWrongAnswerDialog(livesLeft: Int) {
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
        dialogView.findViewById<TextView>(R.id.wrongText).text = "Wrong Answer! Lives left: $livesLeft"
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({ if (dialog.isShowing) dialog.dismiss() }, 1500)
    }

    private fun showRetryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_retry, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogView.findViewById<Button>(R.id.btnRetry).setOnClickListener {
            dialog.dismiss()
            recreate()
        }
        dialogView.findViewById<Button>(R.id.btnMainMenu).setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showCompletionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_completion, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogView.findViewById<TextView>(R.id.dialogMessage).text = "You've mastered Medium Mode! Ready for the ultimate challenge?"
        val btnContinue = dialogView.findViewById<Button>(R.id.btnContinue)
        btnContinue.text = "Continue to Hard"
        btnContinue.setOnClickListener {
            val intent = Intent(this, HardModeActivity::class.java)
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