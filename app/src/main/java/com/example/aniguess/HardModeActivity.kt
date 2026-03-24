package com.example.aniguess

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HardModeActivity : AppCompatActivity() {

    data class Level(
        val name: String,
        val images: List<Int>,
        val choices: List<String>
    )

    private lateinit var allLevels: List<Level>
    private lateinit var shuffledLevels: MutableList<Level>
    private var currentLevelIndex = 0
    
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 20000 // 20 seconds
    private val totalTimeInMillis: Long = 20000
    private lateinit var timerBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hard_mode)

        timerBar = findViewById(R.id.timer)

        initLevels()
        shuffledLevels = allLevels.shuffled().toMutableList()
        loadLevel()

        findViewById<Button>(R.id.homebtn).setOnClickListener {
            stopTimer()
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initLevels() {
        allLevels = listOf(
            Level("Monster", listOf(R.drawable.monster1, R.drawable.monster2, R.drawable.monster3, R.drawable.monster4), listOf("Monster", "Steins;Gate", "Vinland Saga", "Psycho-Pass")),
            Level("Steins;Gate", listOf(R.drawable.steinsgate1, R.drawable.steinsgate2, R.drawable.steinsgate3, R.drawable.steinsgate4), listOf("Monster", "Steins;Gate", "Cowboy Bebop", "Serial Experiments Lain")),
            Level("Vinland Saga", listOf(R.drawable.vinland1, R.drawable.vinland2, R.drawable.vinland3, R.drawable.vinland4), listOf("Vinland Saga", "Berserk", "Vagabond", "Kingdom")),
            Level("Psycho-Pass", listOf(R.drawable.psychopass1, R.drawable.psychopass2, R.drawable.psychopass3, R.drawable.psychopass4), listOf("Psycho-Pass", "Ghost in the Shell", "Akira", "Cyberpunk"))
        )
    }

    private fun loadLevel() {
        if (currentLevelIndex >= shuffledLevels.size) {
            stopTimer()
            showFinalCompletionDialog()
            return
        }

        val level = shuffledLevels[currentLevelIndex]
        val displayImages = level.images.shuffled()
        val displayChoices = level.choices.shuffled()

        updateImages(displayImages[0], displayImages[1], displayImages[2], displayImages[3])
        setButtonText(displayChoices[0], displayChoices[1], displayChoices[2], displayChoices[3])

        findViewById<Button>(R.id.choice1).setOnClickListener { if (displayChoices[0] == level.name) handleCorrectAnswer() else handleWrongAnswer() }
        findViewById<Button>(R.id.choice2).setOnClickListener { if (displayChoices[1] == level.name) handleCorrectAnswer() else handleWrongAnswer() }
        findViewById<Button>(R.id.choice3).setOnClickListener { if (displayChoices[2] == level.name) handleCorrectAnswer() else handleWrongAnswer() }
        findViewById<Button>(R.id.choice4).setOnClickListener { if (displayChoices[3] == level.name) handleCorrectAnswer() else handleWrongAnswer() }

        startTimer()
    }

    private fun startTimer() {
        stopTimer()
        timeLeftInMillis = 20000
        timer = object : CountDownTimer(timeLeftInMillis, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerUI()
                showRetryDialog("Time's Up!")
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun updateTimerUI() {
        val progress = timeLeftInMillis.toFloat() / totalTimeInMillis
        val params = timerBar.layoutParams
        val parentWidth = (timerBar.parent as View).width - 200
        params.width = (parentWidth * progress).toInt()
        timerBar.layoutParams = params
    }

    private fun handleCorrectAnswer() {
        stopTimer()
        showCorrectAnswerDialog()
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            currentLevelIndex++
            loadLevel()
        }, 1500)
    }

    private fun handleWrongAnswer() {
        timeLeftInMillis -= 5000
        if (timeLeftInMillis <= 0) {
            timeLeftInMillis = 0
            stopTimer()
            updateTimerUI()
            showRetryDialog("Wrong Answer!")
        } else {
            val remaining = timeLeftInMillis
            stopTimer()
            timer = object : CountDownTimer(remaining, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimerUI()
                }

                override fun onFinish() {
                    timeLeftInMillis = 0
                    updateTimerUI()
                    showRetryDialog("Time's Up!")
                }
            }.start()
            showWrongAnswerDialog()
        }
    }

    private fun showCorrectAnswerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_correct, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val wlp = dialog.window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        wlp?.y = 300
        dialog.window?.attributes = wlp
        dialog.show()
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ if (dialog.isShowing) dialog.dismiss() }, 1500)
    }

    private fun showWrongAnswerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_wrong, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val wlp = dialog.window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        wlp?.y = 300
        dialog.window?.attributes = wlp
        dialogView.findViewById<TextView>(R.id.wrongText).text = "-5 Seconds!"
        dialog.show()
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ if (dialog.isShowing) dialog.dismiss() }, 1000)
    }

    private fun showRetryDialog(message: String) {
        stopTimer()
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_retry, null)
        dialogView.findViewById<TextView>(R.id.dialogMessage).text = "$message Try again?"
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
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

    private fun showFinalCompletionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_completion, null)
        dialogView.findViewById<TextView>(R.id.dialogMessage).text = "You've conquered Hard Mode! You are a true Anime Legend! Would you like to try again or go back to menu?"
        val btnContinue = dialogView.findViewById<Button>(R.id.btnContinue)
        btnContinue.text = "Retry"
        
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnContinue.setOnClickListener {
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