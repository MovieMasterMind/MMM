package com.example.mmm

import MoviePosterAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.recyclerview.widget.RecyclerView


class SearchableActivity : AppCompatActivity() {

    private lateinit var queryTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoviePosterAdapter

    private val apiKey = "1f443a53a6aabe4de284f9c46a17f64c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

        queryTextView = findViewById(R.id.queryTextView)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun fetchMovieInfo(query: String) {

    }

    // Function to retrieve the previous search query
    private fun getPreviousSearchQuery(): String {
        // For demonstration, assume the previous search query is retrieved from SharedPreferences
        val sharedPreferences = getSharedPreferences("search_preferences", MODE_PRIVATE)
        val previousQuery = sharedPreferences.getString("previous_query", "") ?: ""
        Log.d("SearchableActivity", "Previous search query: $previousQuery")

        return previousQuery
    }

    // Function to display the search query on the screen
    private fun displaySearchQuery(query: String) {
        Log.d("SearchableActivity", "Formatted string: $query")
        val formattedString = getString(R.string.previous_search_query, query)
        queryTextView.text = formattedString
    }

    // Display search bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
