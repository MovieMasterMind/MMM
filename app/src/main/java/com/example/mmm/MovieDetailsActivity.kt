package com.example.mmm

import APICaller
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.mmm.databinding.ActivityMovieDetailsBinding
import org.json.JSONObject
import java.util.Locale

class MovieDetailsActivity : AppCompatActivity() {


    //mind not need
    private var streamingDetails: Map<String, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
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
        val tmdbUrl = "https://api.themoviedb.org/3/movie/$movieId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"
        val apiCaller = APICaller() // Ensure you have an instance of APICaller



        apiCaller.getMovieStreamingLocationJSON(movieId) { streamingDetails ->
            // Log or display the streaming details
            Log.e("StreamingDetails", streamingDetails.toString())

        //streamingDetails hold links of locations of the streaming service


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, tmdbUrl, null,
            { response ->
                // Existing code to display movie details
                displayMovieDetails(response, streamingDetails)

                // New code to fetch and display cast details
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
    }}

    private fun displayMovieDetails(movieDetails: JSONObject, streamingDetails: Map<String, String>) {
        val titleTextView: TextView = findViewById(R.id.movieTitle)
        val overviewTextView: TextView = findViewById(R.id.movieOverview)
        val genreTextView: TextView = findViewById(R.id.movieGenre)
        val steamingTextView: TextView = findViewById(R.id.movieStreamingLocation)
        val posterImageView: ImageView = findViewById(R.id.moviePoster)

        //added for the movie streaming location with link

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


        //here we add text to the streamView
        Log.e("WHY",streamingDetails.toString())
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
            params.setMargins(0, 0, 0, 24) // Add bottom margin to create spacing between buttons
            button.layoutParams = params
            streamingLayout.addView(button)
        }


        // For stars, you may need to use another API call or a fixed value, as TMDB API doesn't directly provide star ratings in the way users might expect (like 5 stars out of 5)

        val posterPath = movieDetails.getString("poster_path")
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$posterPath")
            //.override(400, 600) // Specify the desired width and height
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
}
