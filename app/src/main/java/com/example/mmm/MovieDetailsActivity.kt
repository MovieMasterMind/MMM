package com.example.mmm

import APICaller
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class MovieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_movie_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        if (movieId != -1) {
            fetchMovieDetails(movieId)
        } else {
            finish() // Close the activity if movie ID wasn't passed correctly
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        val url = "https://api.themoviedb.org/3/movie/$movieId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                // Existing code to display movie details
                displayMovieDetails(response)

                // New code to fetch and display cast details
                val apiCaller = APICaller() // Ensure you have an instance of APICaller
                apiCaller.getCastDetails(movieId) { castList ->
                    val castString = castList.joinToString(", ")
                    runOnUiThread {
                        findViewById<TextView>(R.id.movieCast).text = getString(R.string.cast_format, castString)
                    }
                }
            },
            { error ->
                Log.e("MovieDetailsActivity", "Error fetching movie details: $error")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun displayMovieDetails(movieDetails: JSONObject) {
        val titleTextView: TextView = findViewById(R.id.movieTitle)
        val overviewTextView: TextView = findViewById(R.id.movieOverview)
        val genreTextView: TextView = findViewById(R.id.movieGenre)
        val posterImageView: ImageView = findViewById(R.id.moviePoster)

        titleTextView.text = movieDetails.getString("title")
        overviewTextView.text = movieDetails.getString("overview")
        val releaseDateTextView: TextView = findViewById(R.id.movieReleaseDate)
        releaseDateTextView.text = movieDetails.getString("release_date")


        val genresArray = movieDetails.getJSONArray("genres")
        val genreNames = mutableListOf<String>()
        for (i in 0 until genresArray.length()) {
            val genre = genresArray.getJSONObject(i)
            genreNames.add(genre.getString("name"))
        }
        genreTextView.text = genreNames.joinToString(", ")

        // For stars, you may need to use another API call or a fixed value, as TMDB API doesn't directly provide star ratings in the way users might expect (like 5 stars out of 5)

        val posterPath = movieDetails.getString("poster_path")
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$posterPath")
            .into(posterImageView)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }
}
