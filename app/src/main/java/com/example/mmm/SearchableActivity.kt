package com.example.mmm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class SearchableActivity : AppCompatActivity() {

    private lateinit var queryTextView: TextView
    private val apiKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

        queryTextView = findViewById(R.id.queryTextView)

        val previousQuery = intent.getStringExtra("QUERY") ?: ""
        queryTextView.text = previousQuery

        // Fetch movie information based on the search query
        fetchMovieInfo(previousQuery)
    }

    private fun fetchMovieInfo(query: String) {
        lifecycleScope.launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$query")
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                val jsonData = response.body?.string()

                if (jsonData != null) {
                    val jsonObject = JSONObject(jsonData)
                    val results = jsonObject.getJSONArray("results")

                    if (results.length() > 0) {
                        val movie = results.getJSONObject(0)
                        val title = movie.getString("title")
                        val overview = movie.getString("overview")

                        // Update UI with movie information
                        findViewById<TextView>(R.id.movieTitle).text = title
                        findViewById<TextView>(R.id.movieDescription).text = overview
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchableActivity", "Error fetching movie information: ${e.message}")
            }
        }
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
