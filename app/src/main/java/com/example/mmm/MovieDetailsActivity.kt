package com.example.mmm

import APICaller
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.mmm.databinding.ActivityMovieDetailsBinding
import org.json.JSONObject
import java.util.Locale


        class MovieDetailsActivity : AppCompatActivity() {
            private var streamingDetails: Map<String, String> = emptyMap()

            private lateinit var movieDetailsObj: JSONObject
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_movie_details)

                val toolbar: androidx.appcompat.widget.Toolbar =
                    findViewById(R.id.toolbar_movie_details)
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
                        Toast.makeText(this, "Movie details not loaded yet", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            private fun fetchMovieDetails(movieId: Int) {
                val url =
                    "https://api.themoviedb.org/3/movie/$movieId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

                val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        // Existing code to display movie details
                        displayMovieDetails(response)

                        // New code to fetch and display cast details
                        val apiCaller = APICaller() // Ensure you have an instance of APICaller
                        apiCaller.getCastDetails(movieId) { castList ->
                            val castString = castList.joinToString(", ")
                            runOnUiThread {
                                findViewById<TextView>(R.id.movieCast).text =
                                    getString(R.string.cast_format, castString)
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


                //here we add text to the streamView
                Log.e("WHY", streamingDetails.toString())
                //steamingTextView.text = streamingDetails

                val streamingJson = JSONObject(streamingDetails)
                val streamingLayout: LinearLayout = findViewById(R.id.streamingLayout)


                // Create buttons for streaming services
                for ((service, link) in streamingDetails) {
                    val button = Button(this)
                    button.text = service
                    val colors = getColorForService(service)
                    colors?.let { (backgroundColor, textColor) ->
                        button.setBackgroundColor(backgroundColor)
                        button.setTextColor(textColor)
                    }
                    button.setOnClickListener {
                        // Open the link in the browser when the button is clicked
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(link)
                        startActivity(intent)
                    }
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(
                        0,
                        0,
                        0,
                        24
                    ) // Add bottom margin to create spacing between buttons
                    button.layoutParams = params
                    streamingLayout.addView(button)
                }


                // For stars, you may need to use another API call or a fixed value, as TMDB API doesn't directly provide star ratings in the way users might expect (like 5 stars out of 5)

                val posterPath = movieDetails.getString("poster_path")
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500$posterPath")
                    .into(posterImageView)
            }

            private val serviceColorsMap = mapOf(
                "netflix" to Pair(Color.BLACK, Color.parseColor("#E50914")),
                "prime" to Pair(Color.parseColor("#00A8E1"), Color.BLACK),
                "hulu" to Pair(Color.parseColor("#1CE783"), Color.BLACK),
                "apple" to Pair(Color.GRAY, Color.BLACK),
                "peacock" to Pair(Color.BLACK, Color.WHITE),
                "hbo" to Pair(Color.WHITE, Color.BLACK)


                // Add more services and colors as needed
            )

            private fun getColorForService(service: String): Pair<Int, Int>? {
                return serviceColorsMap[service.lowercase(Locale.ROOT)]
            }


            override fun onSupportNavigateUp(): Boolean {
                finish() // Close this activity and return to the previous one
                return true
            }

            @SuppressLint("MutatingSharedPrefs")
            private fun addToWatchlist(movieId: Int, movieTitle: String, moviePosterUrl: String) {
                val sharedPrefs = getSharedPreferences("watchlist", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()

                // Create a composite string of movie details
                val movieDetails = "$movieId|$movieTitle|$moviePosterUrl"

                // Retrieve the current watchlist, add the new item, and save it back
                val watchlist =
                    sharedPrefs.getStringSet("watchlist", mutableSetOf()) ?: mutableSetOf()
                watchlist.add(movieDetails) //need to be suppress (whatever that means)
                editor.putStringSet("watchlist", watchlist).apply()

                Toast.makeText(this, "Added to watchlist", Toast.LENGTH_SHORT).show()
            }
        }


