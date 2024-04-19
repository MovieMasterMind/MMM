package com.example.mmm

import APICaller
import MoviePoster
import SearchResultAdapter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        intent.getStringExtra("QUERY")?.let {
            queryTextView.text = getString(R.string.search_results, it)
            fetchMovieInfo(it)
        }
    }

    private fun fetchMovieInfo(query: String) {
        val apiUrl = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$query"
        APICaller().fetchData(apiUrl, this::parseSearchResults) { imageUrls, movieTitles, movieIds ->
            val items = List(movieTitles.size) { index ->
                MoviePoster(movieTitles[index], imageUrls[index], movieIds[index])
            }
            runOnUiThread {
                adapter = SearchResultAdapter(items)
                recyclerViewResults.adapter = adapter
            }
        }
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
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search for movies"

        val searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searchEditText.setTextColor(Color.WHITE)
        searchEditText.setHintTextColor(Color.WHITE)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handler.removeCallbacks(updateRunnable)
                query?.let {
                    queryTextView.text = getString(R.string.search_results, it)
                    fetchMovieInfo(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handler.removeCallbacks(updateRunnable)
                newText?.let {
                    if (it.length >= 3) {
                        handler.postDelayed(updateRunnable, 500) // Defer fetching movie info for 500 ms
                    }
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

}