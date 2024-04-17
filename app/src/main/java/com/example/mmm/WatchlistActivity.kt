package com.example.mmm

import WatchlistAdapter
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WatchlistActivity : AppCompatActivity() {

    private lateinit var watchlistRecyclerView: RecyclerView
    private lateinit var watchlistAdapter: WatchlistAdapter
    private var watchlistItems: MutableList<WatchlistItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_movie_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        watchlistRecyclerView = findViewById(R.id.watchlist_recycler_view)
        watchlistRecyclerView.layoutManager = LinearLayoutManager(this)

        loadWatchlist()
    }

    private fun loadWatchlist() {
        val sharedPrefs = getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val watchlistJson = sharedPrefs.getString("watchlistJson", "[]")

        val gson = Gson()
        val type = object : TypeToken<MutableList<WatchlistItem>>() {}.type
        watchlistItems = gson.fromJson(watchlistJson, type)

        watchlistAdapter = WatchlistAdapter(watchlistItems).apply {
            // If the adapter has a custom method to set a callback or listener, set it here
        }
        watchlistRecyclerView.adapter = watchlistAdapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh the watchlist in case of any changes made in the details view or elsewhere
        loadWatchlist()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }
}
