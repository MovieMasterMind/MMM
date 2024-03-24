package com.example.mmm
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
        // Get the movie ID from the intent
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        if (movieId != -1) {
            fetchMovieDetails(movieId)
        } else {
            // Handle the case where the movie ID wasn't passed correctly
            finish() // Close the activity, or show an error message
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

    private fun fetchMovieDetails(movieId: Int) {
        val url = "https://api.themoviedb.org/3/movie/$movieId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                displayMovieDetails(response)
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
        val releaseDateTextView: TextView = findViewById(R.id.movieReleaseDate)
        val posterImageView: ImageView = findViewById(R.id.moviePoster)

        titleTextView.text = movieDetails.getString("title")
        overviewTextView.text = movieDetails.getString("overview")
        releaseDateTextView.text = movieDetails.getString("release_date")

        val posterPath = movieDetails.getString("poster_path")
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$posterPath")
            .into(posterImageView)
    }
}
