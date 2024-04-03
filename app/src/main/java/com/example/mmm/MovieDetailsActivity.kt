package com.example.mmm

import APICaller
import CastAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieDetailsObj: JSONObject
    private val apiCaller = APICaller()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_movie_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
//        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        if (movieId != -1) {
            fetchMovieDetails(movieId)
        } else {
            finish() // Close the activity if movie ID wasn't passed correctly
        }
        val addToWatchlistButton: Button = findViewById(R.id.addToWatchlistButton)
        addToWatchlistButton.setOnClickListener {
            // Now check if movieDetails is initialized before using it
            if (this::movieDetailsObj.isInitialized) {
                val movieTitle = movieDetailsObj.getString("title")
                val moviePosterPath = movieDetailsObj.getString("poster_path")
                val moviePosterUrl = "https://image.tmdb.org/t/p/w500$moviePosterPath"
                addToWatchlist(movieId, movieTitle, moviePosterUrl)
            } else {
                Toast.makeText(this, "Movie details not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        val url = "https://api.themoviedb.org/3/movie/$movieId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                // Existing code to display movie details
                displayMovieDetails(response)

//                // New code to fetch and display cast details
//                val apiCaller = APICaller() // Ensure you have an instance of APICaller
//                apiCaller.getCastDetails(movieId) { castList ->
//                    val castString = castList.joinToString(", ")
//                    runOnUiThread {
//                        findViewById<TextView>(R.id.movieCast).text = getString(R.string.cast_format, castString)
//                    }
//                }
            },
            { error ->
                Log.e("MovieDetailsActivity", "Error fetching movie details: $error")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun displayMovieDetails(movieDetails: JSONObject) {
        movieDetailsObj = movieDetails
        val titleTextView: TextView = findViewById(R.id.movieTitle)
        val overviewTextView: TextView = findViewById(R.id.movieOverview)
        val genreTextView: TextView = findViewById(R.id.movieGenre)
        val posterImageView: ImageView = findViewById(R.id.moviePoster)
        val voteAverageTextView: TextView = findViewById(R.id.movieVoteAverage)

        titleTextView.text = movieDetails.getString("title")
        overviewTextView.text = movieDetails.getString("overview")
        val releaseDateTextView: TextView = findViewById(R.id.movieReleaseDate)
        releaseDateTextView.text = movieDetails.getString("release_date")
        val voteAverage = movieDetails.getDouble("vote_average")
        voteAverageTextView.text = getString(R.string.vote_average_format, voteAverage)

        val genresArray = movieDetails.getJSONArray("genres")
        val genreNames = mutableListOf<String>()
        for (i in 0 until genresArray.length()) {
            val genre = genresArray.getJSONObject(i)
            genreNames.add(genre.getString("name"))
        }
        genreTextView.text = genreNames.joinToString(", ")

        // Load movie poster
        val posterPath = movieDetails.getString("poster_path")
        val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
        Glide.with(this).load(posterUrl).into(posterImageView)

        // Initialize RecyclerView and its adapter for cast members
        val castRecyclerView: RecyclerView = findViewById(R.id.castRecyclerView)
        val castAdapter = CastAdapter(emptyList()) // Initialize with an empty list
        castRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        castRecyclerView.adapter = castAdapter

        // Fetch and display cast details
        apiCaller.getCastDetails(movieDetails.getInt("id")) { castList ->
            runOnUiThread {
                castAdapter.updateCastList(castList)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

    private fun addToWatchlist(movieId: Int, movieTitle: String, moviePosterUrl: String) {
        val sharedPrefs = getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val watchlistJson = sharedPrefs.getString("watchlistJson", "[]")

        val gson = Gson()
        val type = object : TypeToken<MutableList<WatchlistItem>>() {}.type
        val watchlist: MutableList<WatchlistItem> = gson.fromJson(watchlistJson, type)

        // Check if the movie is already in the watchlist
        if (watchlist.any { it.movieId == movieId }) {
            // Movie is already in the watchlist, show a toast message
            Toast.makeText(this, "Movie is already in the watchlist", Toast.LENGTH_SHORT).show()
        } else {
            // Movie is not in the watchlist, add it
            watchlist.add(WatchlistItem(movieId, movieTitle, moviePosterUrl))

            // Convert the updated list back into JSON
            val updatedJson = gson.toJson(watchlist, type)

            // Save the updated JSON in SharedPreferences
            with(sharedPrefs.edit()) {
                putString("watchlistJson", updatedJson)
                apply()
            }

            // Show a toast message confirming the addition
            Toast.makeText(this, "Movie added to watchlist", Toast.LENGTH_SHORT).show()
        }
    }


}

