package com.example.mmm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EpisodeDetailActivity : AppCompatActivity() {

    private lateinit var tvShowIdView: TextView
    private lateinit var seasonIdView: TextView
    private lateinit var episodeNumberView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        tvShowIdView = findViewById(R.id.tvShowId)
        seasonIdView = findViewById(R.id.seasonId)
        episodeNumberView = findViewById(R.id.episodeNumber)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_episode_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Retrieve the data from the intent
        intent?.extras?.let {
            val tvShowId = it.getInt("TV_SHOW_ID", -1)
            val seasonId = it.getInt("SEASON_ID", -1)
            val episodeNumber = it.getString("EPISODE_NUMBER", "N/A")

            // Display the data
            tvShowIdView.text = "TV Show ID: $tvShowId"
            seasonIdView.text = "Season ID: $seasonId"
            episodeNumberView.text = "Episode Number: $episodeNumber"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }
}
