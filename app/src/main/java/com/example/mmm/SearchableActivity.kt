package com.example.mmm

import APICaller
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchableActivity : AppCompatActivity() {

    private lateinit var queryTextView: TextView
    private lateinit var recyclerViewResults: RecyclerView
    private lateinit var adapter: MoviePosterAdapter

    private val apiKey = "1f443a53a6aabe4de284f9c46a17f64c"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search_results) // Make sure this ID matches your layout
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchQueryName = findViewById<TextView>(R.id.query_search_results)
        val query = intent.getStringExtra("QUERY")

        if (query != null) {
            // Pass movie name to dynamic string and print query
            searchQueryName.text = getString(R.string.search_results, query)

            fetchMovieInfo(query)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Pass search query to setUpRecyclerView
    private fun fetchMovieInfo(query: String) {
        val apiUrl = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$query"
        queryTextView = findViewById(R.id.queryTextView)
        recyclerViewResults = findViewById(R.id.recyclerViewResults)

        setUpRecyclerView(apiUrl, queryTextView, recyclerViewResults)
    }

    // Do the search and display results
    private fun setUpRecyclerView(apiUrl: String, textView: TextView, recyclerView: RecyclerView) {
        // Set up layout manager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        // Create an instance of the adapter with layout params
        adapter = MoviePosterAdapter(emptyList(), emptyList())
        // Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

        val apiCaller = APICaller() // Create an instance of APICaller

        // Get data from API and update the adapter
        apiCaller.getData(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
            // Run on UI thread since response callback is on a background thread
            runOnUiThread {
                // Create a new adapter with the data
                adapter = MoviePosterAdapter(posterUrls, movieIds)
                recyclerView.adapter = adapter
            }
        }

    }


    // Add search functionality in search result page
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.options_menu, menu)

        // Display search bar
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search for movies"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Called when the user submits final query
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Navigate to the search_results activity
                query?.let {
                    fetchMovieInfo(it)
                }
                return true
            }

            // Called everytime a character is changed in the query
            override fun onQueryTextChange(newText: String?): Boolean {
                // Not needed for this case
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}