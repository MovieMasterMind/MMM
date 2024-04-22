package com.example.mmm

import APICaller
import CastAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.Locale


class MovieDetailsActivity : AppCompatActivity() {
    private var streamingDetails: Map<String, String> = emptyMap()
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
        if (movieId != -1) {
            fetchMovieDetails(movieId)
            initializeWatchlistButton(movieId)
            getRecommendations(movieId)
        } else {
            finish() // Close the activity if movie ID wasn't passed correctly
        }
    }

    private fun initializeWatchlistButton(movieId: Int) {
        val sharedPrefs = getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<List<WatchlistItem>>() {}.type
        val watchlist: List<WatchlistItem> = gson.fromJson(sharedPrefs.getString("watchlistJson", "[]"), type)

        val isMovieInWatchlist = watchlist.any { it.movieId == movieId }

        val addToWatchlistButton: Button = findViewById(R.id.addToWatchlistButton)
        updateButtonAppearanceAndAction(isMovieInWatchlist, addToWatchlistButton, movieId)
    }

    private fun updateButtonAppearanceAndAction(isInWatchlist: Boolean, button: Button, movieId: Int) {
        button.text = if (isInWatchlist) {
            getString(R.string.remove_from_watchlist)
        } else {
            getString(R.string.add_to_watchlist)
        }

        // Update button appearance based on whether the movie is in the watchlist
        button.setBackgroundColor(ContextCompat.getColor(this, if (isInWatchlist) R.color.remove_from_watchlist_background else R.color.add_to_watchlist_background))

        button.setOnClickListener {
            if (this::movieDetailsObj.isInitialized) {
                val movieTitle = movieDetailsObj.getString("title")
                val moviePosterPath = movieDetailsObj.getString("poster_path")
                val moviePosterUrl = "https://image.tmdb.org/t/p/w500$moviePosterPath"
                checkAddOrRemoveFromWatchlist(movieId, movieTitle, moviePosterUrl)
                updateButtonAppearanceAndAction(!isInWatchlist, button, movieId)
            } else {
                Toast.makeText(this, "Movie details not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAddOrRemoveFromWatchlist(movieId: Int, movieTitle: String, moviePosterUrl: String) {
        val sharedPrefs = getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<WatchlistItem>>() {}.type
        var watchlist: MutableList<WatchlistItem> = gson.fromJson(sharedPrefs.getString("watchlistJson", "[]"), type)

        val itemIndex = watchlist.indexOfFirst { it.movieId == movieId }
        if (itemIndex != -1) {
            // Movie is already in the watchlist, remove it
            watchlist.removeAt(itemIndex)
            Toast.makeText(this, "Removed from watchlist", Toast.LENGTH_SHORT).show()
        } else {
            // Movie is not in the watchlist, add it
            watchlist.add(WatchlistItem(movieId, movieTitle, moviePosterUrl))
            Toast.makeText(this, "Added to watchlist", Toast.LENGTH_SHORT).show()
        }

        // Save the updated watchlist back to SharedPreferences
        sharedPrefs.edit().putString("watchlistJson", gson.toJson(watchlist, type)).apply()
    }

    private fun getRecommendations(movieId: Int) {
        val url =
            "https://api.themoviedb.org/3/movie/$movieId/recommendations"
    }

    private fun fetchMovieDetails(movieId: Int) {
        val url =
            "https://api.themoviedb.org/3/movie/$movieId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

        apiCaller.getMovieStreamingLocationJSON(movieId) { streamingDetails ->
            Log.e("StreamingDetails", streamingDetails.toString())

            //streamingDetails hold links of locations of the streaming service
            val streamingDetailsFound = streamingDetails.isNotEmpty()
            val textViewText = if (streamingDetailsFound) {
                // If details are found, hide the TextView and set text to empty
                runOnUiThread {
                    findViewById<TextView>(R.id.titleBeforeStreamingServices).apply {
                        text = ""
                        visibility = View.GONE  // Hide the TextView
                    }
                }
            } else {
                // If no details are found, show the TextView and set the no-data message
                runOnUiThread {
                    findViewById<TextView>(R.id.titleBeforeStreamingServices).apply {
                        text = "No streaming platforms found :("
                        visibility = View.VISIBLE  // Show the TextView
                    }
                }
            }

            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    // Existing code to display movie details
                    displayMovieDetails(response, streamingDetails)
                },
                { error ->
                    Log.e("MovieDetailsActivity", "Error fetching movie details: $error")
                }
            )

            Volley.newRequestQueue(this).add(jsonObjectRequest)
        }
    }

    private fun displayMovieDetails(movieDetails: JSONObject, streamingDetails: Map<String, String>) {
        movieDetailsObj = movieDetails
        val titleTextView: TextView = findViewById(R.id.movieTitle)
        val overviewTextView: TextView = findViewById(R.id.movieOverview)
        val genreTextView: TextView = findViewById(R.id.movieGenre)
        val posterImageView: ImageView = findViewById(R.id.moviePoster)
        val voteAverageTextView: TextView = findViewById(R.id.movieVoteAverage)
        val runTimeTextView: TextView = findViewById(R.id.movieRuntime)

        titleTextView.text = movieDetails.getString("title")
        overviewTextView.text = movieDetails.getString("overview")
        val releaseDateTextView: TextView = findViewById(R.id.movieReleaseDate)
        releaseDateTextView.text = movieDetails.getString("release_date")
        val voteAverage = movieDetails.getDouble("vote_average")
        voteAverageTextView.text = getString(R.string.vote_average_format, voteAverage)
        var runTime = movieDetails.getInt("runtime")
        var hours = 0
        while (runTime > 60) {
            runTime = runTime - 60
            hours = hours + 1
        }

        runTimeTextView.text = getString(R.string.runtime, hours, runTime )

        val genresArray = movieDetails.getJSONArray("genres")
        val genreNames = mutableListOf<String>()
        for (i in 0 until genresArray.length()) {
            val genre = genresArray.getJSONObject(i)
            genreNames.add(genre.getString("name"))
        }
        genreTextView.text = genreNames.joinToString(", ")

        val posterPath = movieDetails.getString("poster_path")
        if (posterPath != "null") {
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            Glide.with(this).load(posterUrl).into(posterImageView)
        } else {
            val posterUrl =  "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.IkbcciGb75wX7U5WeANuDQHaLE%26pid%3DApi&f=1&ipt=a36f8b1fdf094f9245bbbfbf6e0d0908dd49b8c2ae8557f5632a06cce2c36cf2&ipo=images"
            Glide.with(this).load(posterUrl).into(posterImageView)
        }


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



        Log.e("WHY", streamingDetails.toString())


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

    }

    private val serviceColorsMap = mapOf(
        "netflix" to Pair(Color.BLACK, Color.parseColor("#E50914")),
        "prime" to Pair(Color.parseColor("#00A8E1"), Color.BLACK),
        "hulu" to Pair(Color.parseColor("#1CE783"), Color.BLACK),
        "apple" to Pair(Color.GRAY, Color.BLACK),
        "peacock" to Pair(Color.BLACK, Color.WHITE),
        "hbo" to Pair(Color.WHITE, Color.BLACK),
        "disney" to Pair(Color.parseColor("#000137"), Color.parseColor("#FFFFFF")),
        "paramount" to Pair(Color.parseColor("#0164FF"), Color.parseColor("#FFFFFF"))


        // Add more services and colors as needed - "service" to Pair(Color.color, Color.color), -
    )

    private fun getColorForService(service: String): Pair<Int, Int>? {
        return serviceColorsMap[service.lowercase(Locale.ROOT)]
    }
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

}