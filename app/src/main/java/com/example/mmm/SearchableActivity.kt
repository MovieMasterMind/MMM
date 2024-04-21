package com.example.mmm

import APICaller
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        val appliedFilters = intent.getStringArrayListExtra("FILTERS")

        if (query != null) {
            searchQueryName.text = getString(R.string.search_results, query)
            val apiUrl = constructApiUrl(query, appliedFilters)
            fetchMovieInfo(apiUrl)
        }
    }

    private fun constructApiUrl(query: String?, appliedFilters: List<String>?): String {
        val baseUrl = "https://api.themoviedb.org/3/search/movie"
        val queryParams = mutableListOf<String>()

        query?.let {
            queryParams.add("query=${it.trim()}")
        }
        appliedFilters?.let { filters ->
            if (filters.isNotEmpty()) {
                val genreQuery = filters.joinToString(",") // Join multiple genre IDs with ","
                queryParams.add("with_genres=$genreQuery")
            }
        }
        queryParams.add("api_key=$apiKey")

        return "$baseUrl?${queryParams.joinToString("&")}"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun fetchMovieInfo(apiUrl: String) {
        queryTextView = findViewById(R.id.queryTextView)
        recyclerViewResults = findViewById(R.id.recyclerViewResults)

        setUpRecyclerView(apiUrl, queryTextView, recyclerViewResults)
    }

    private fun setUpRecyclerView(apiUrl: String, textView: TextView, recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        adapter = MoviePosterAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        val apiCaller = APICaller()

        apiCaller.getData(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
            runOnUiThread {
                adapter = MoviePosterAdapter(posterUrls, movieIds)
                recyclerView.adapter = adapter
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                val filterListener = object : FilterDialogFragment.FilterListener {
                    override fun onFiltersApplied(selectedFilters: List<String>) {
                        val query = queryTextView.text.toString() // Get the current query
                        val apiUrl = constructApiUrl(query, selectedFilters)
                        fetchMovieInfo(apiUrl)
                    }
                }

                // Create FilterDialogFragment and set the filter listener
                val dialogFragment = FilterDialogFragment()
                dialogFragment.setFilterListener(filterListener)

                // Show the dialog fragment
                dialogFragment.show(supportFragmentManager, "FilterDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search for movies"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Called when the user submits final query
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    fetchMovieInfo(it)
                }
                return true
            }

            // Called everytime a character is changed in the query
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    fetchMovieInfo(it)
                }
                return true

            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}