package com.example.mmm

import WatchlistAdapter
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WatchlistActivity : AppCompatActivity() {

    private lateinit var watchlistRecyclerView: RecyclerView
    private lateinit var watchlistAdapter: WatchlistAdapter
    private lateinit var emptyView: TextView
    private var watchlistItems: MutableList<WatchlistItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_movie_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        watchlistRecyclerView = findViewById(R.id.watchlist_recycler_view)
        emptyView = findViewById(R.id.empty_view) // Initialize the empty view
        watchlistRecyclerView.layoutManager = LinearLayoutManager(this)

        loadWatchlist()
    }

    private fun loadWatchlist() {
        val sharedPrefs = getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val watchlistJson = sharedPrefs.getString("watchlistJson", "[]")
        val gson = Gson()
        val type = object : TypeToken<MutableList<WatchlistItem>>() {}.type
        watchlistItems = gson.fromJson(watchlistJson, type)

        watchlistAdapter = WatchlistAdapter(watchlistItems)
        watchlistRecyclerView.adapter = watchlistAdapter

        checkEmpty()  // Check if the list is empty and update the UI
    }

    fun checkEmpty() {
        if (watchlistItems.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            watchlistRecyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            watchlistRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadWatchlist()  // Ensure that the watchlist is up-to-date
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }
}

