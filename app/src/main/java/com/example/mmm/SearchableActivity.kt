package com.example.mmm

import APICaller
import MoviePoster
import SearchResultAdapter
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject


class SearchableActivity : AppCompatActivity() {
    private lateinit var queryTextView: TextView
    private lateinit var recyclerViewResults: RecyclerView
    private lateinit var adapter: SearchResultAdapter
    private lateinit var searchView: SearchView
    private val apiKey = "1f443a53a6aabe4de284f9c46a17f64c"
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = Runnable {
        val currentText = searchView.query.toString()
        queryTextView.text = getString(R.string.search_results, currentText)
        fetchMovieInfo(currentText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search_results)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        queryTextView = findViewById(R.id.queryTextView)
        recyclerViewResults = findViewById(R.id.recyclerViewResults)
        recyclerViewResults.layoutManager = LinearLayoutManager(this)

        adapter = SearchResultAdapter(mutableListOf())
        recyclerViewResults.adapter = adapter

        // Do not attempt to find searchView here, as the menu hasn't been created yet
        // displayHistory() can be called here only if it doesn't depend on searchView
    }

    private fun fetchMovieInfo(query: String) {
        if (query.isEmpty()) {
            displayHistory()
        } else {
            val apiUrl = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$query&sort_by=popularity.desc"
            APICaller().fetchData(apiUrl, this::parseSearchResults) { imageUrls, movieTitles, movieIds ->
                val items = mutableListOf<MoviePoster>()
                for (index in movieTitles.indices) {
                    items.add(MoviePoster(movieTitles[index], imageUrls[index], movieIds[index]))
                }
                runOnUiThread {
                    if (items.isNotEmpty()) {
                        findViewById<TextView>(R.id.emptyHistoryView).visibility = View.GONE
                        recyclerViewResults.visibility = View.VISIBLE
                        adapter.updateData(items)
                    } else {
                        findViewById<TextView>(R.id.emptyHistoryView).visibility = View.VISIBLE
                        recyclerViewResults.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun displayHistory() {
        val sharedPreferences = getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<MoviePoster>>() {}.type
        val historyJson = sharedPreferences.getString("historyJson", "[]")
        val history = gson.fromJson<MutableList<MoviePoster>>(historyJson, type) ?: mutableListOf()

        if (history.isEmpty() && searchView.query.toString().isEmpty()) {
            findViewById<TextView>(R.id.emptyHistoryView).visibility = View.VISIBLE
            recyclerViewResults.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.emptyHistoryView).visibility = View.GONE
            recyclerViewResults.visibility = View.VISIBLE
            adapter.updateData(history)
        }
    }


    private fun performSearch(query: String) {
        // API call to fetch data based on the query
        fetchMovieInfo(query)
    }

    private fun parseSearchResults(response: String): Triple<List<String>, List<String>, List<String>> {
        val posterUrls = mutableListOf<String>()
        val movieTitles = mutableListOf<String>()
        val movieIds = mutableListOf<String>()

        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")
            for (i in 0 until resultsArray.length()) {
                val movieObject = resultsArray.getJSONObject(i)
                val title = movieObject.getString("title")
                val releaseDate = movieObject.optString("release_date", "") // Get release_date or empty string if null
                val releaseYear = if (releaseDate.isNotEmpty() && releaseDate.length >= 4) releaseDate.substring(0, 4) else "" // Extract year from release_date
                val id = movieObject.getInt("id").toString()
                val formattedTitle = if (releaseYear.isNotEmpty()) "$title ($releaseYear)" else title // Append year to title if available
                val posterPath = movieObject.optString("poster_path", "")
                val posterUrl = if (posterPath != "null") "https://image.tmdb.org/t/p/w500$posterPath"
                else "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmedia.istockphoto.com%2Fvectors%2Fno-image-available-icon-vector-id1216251206%3Fk%3D20%26m%3D1216251206%26s%3D170667a%26w%3D0%26h%3DA72dFkHkDdSfmT6iWl6eMN9t_JZmqGeMoAycP-LMAw4%3D&f=1&nofb=1"  // Fallback for no image

                posterUrls.add(posterUrl)
                movieTitles.add(formattedTitle)
                movieIds.add(id)
            }
        } catch (e: JSONException) {
            Log.e("Search JSON Parsing", "Error parsing JSON: $e")
        }

        return Triple(posterUrls, movieTitles, movieIds)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        // Configure the SearchView
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        setupSearchView()

        // Add a clear history menu item programmatically
        menu.add(0, Menu.FIRST, Menu.NONE, "Clear History").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

        // Now that searchView has been initialized, displayHistory() can be safely called
        displayHistory()

        return true
    }

    private fun setupSearchView() {
        searchView.apply {
            findViewById<TextView>(androidx.appcompat.R.id.search_src_text).apply {
                setTextColor(Color.WHITE)
                setHintTextColor(Color.GRAY)
            }
            queryHint = "Search for movies"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { performSearch(it) }
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        displayHistory()
                    } else {
                        performSearch(newText)
                    }
                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            Menu.FIRST -> {
                clearSearchHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearSearchHistory() {
        val sharedPreferences = getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("historyJson").apply()
        displayHistory()  // Refresh the display, showing the empty state if necessary
        Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

    override fun onResume() {
        super.onResume()
        if (::searchView.isInitialized && searchView.query.toString().isEmpty()) {
            displayHistory()  // Call this only if searchView has been initialized and the query is empty
        }
    }

}