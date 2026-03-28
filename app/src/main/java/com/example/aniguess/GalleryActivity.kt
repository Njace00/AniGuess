package com.example.aniguess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {

    data class AnimeInfo(val name: String, val drawableId: Int)

    private val allAnimes = listOf(
        AnimeInfo("Pokemon", R.drawable.pokemon1),
        AnimeInfo("One Piece", R.drawable.onepiece1),
        AnimeInfo("Naruto", R.drawable.naruto1),
        AnimeInfo("Dragon Ball", R.drawable.dragonball1),
        AnimeInfo("Fullmetal Alchemist", R.drawable.fullmetal1),
        AnimeInfo("Bleach", R.drawable.bleach1),
        AnimeInfo("Attack on Titan", R.drawable.attackontitan1),
        AnimeInfo("Hunter x Hunter", R.drawable.hunterxhunter1),
        AnimeInfo("Monster", R.drawable.monster1),
        AnimeInfo("Steins;Gate", R.drawable.steinsgate1),
        AnimeInfo("Vinland Saga", R.drawable.vinland1),
        AnimeInfo("Psycho-Pass", R.drawable.psychopass1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        loadGalleryRecords()

        findViewById<Button>(R.id.homebtn).setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadGalleryRecords() {
        val sharedPref = getSharedPreferences("GalleryPrefs", Context.MODE_PRIVATE)
        
        // Find animes that have been completed
        val completedAnimes = allAnimes.filter { anime ->
            sharedPref.contains("LevelTime_${anime.name}")
        }

        if (completedAnimes.isEmpty()) {
            findViewById<TextView>(R.id.emptyMsg).visibility = View.VISIBLE
            return
        }

        findViewById<TextView>(R.id.emptyMsg).visibility = View.GONE

        // Sort by time taken (longest first) to highlight the hardest challenges
        val sortedAnimes = completedAnimes.sortedByDescending { anime ->
            sharedPref.getLong("LevelTime_${anime.name}", 0)
        }

        // Populate the 12 item slots
        for (i in 1..12) {
            val itemLayoutId = resources.getIdentifier("item$i", "id", packageName)
            val picId = resources.getIdentifier("pic$i", "id", packageName)
            val nameId = resources.getIdentifier("name$i", "id", packageName)
            
            val itemLayout = findViewById<LinearLayout>(itemLayoutId)
            
            if (i <= sortedAnimes.size) {
                val anime = sortedAnimes[i - 1]
                val time = sharedPref.getLong("LevelTime_${anime.name}", 0)
                
                itemLayout.visibility = View.VISIBLE
                findViewById<ImageView>(picId).setImageResource(anime.drawableId)
                findViewById<TextView>(nameId).text = "${anime.name}\n(${time}s)"
            } else {
                itemLayout.visibility = View.GONE
            }
        }

        // Highlight the single longest record at the top title
        val hardest = sortedAnimes[0]
        val longestTime = sharedPref.getLong("LevelTime_${hardest.name}", 0)
        findViewById<TextView>(R.id.difficultytxt).text = "HALL OF CHALLENGES\nHardest: ${hardest.name} (${longestTime}s)"
    }
}