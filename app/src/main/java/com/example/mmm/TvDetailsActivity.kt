package com.example.mmm

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.util.Locale


class TvDetailsActivity : AppCompatActivity() {
    private lateinit var tvDetailsObj: JSONObject
    private val apiCallerForTV = APICallerForTV()
    private lateinit var adapter: TVPosterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_tv_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val tvId = intent.getIntExtra("TV_ID", -1)
        if (tvId != -1) {
            fetchTVDetails(tvId)
        } else {
            finish() // Close the activity if tv ID wasn't passed correctly
        }
    }

    private fun fetchTVDetails(tvId: Int) {
        val url =
            "https://api.themoviedb.org/3/tv/$tvId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

        apiCallerForTV.getTVStreamingLocationJSON(tvId) { streamingDetails ->
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
                    displayTVDetails(response, streamingDetails)
                },
                { error ->
                    Log.e("TVDetailsActivity", "Error fetching movie details: $error")
                }
            )

            Volley.newRequestQueue(this).add(jsonObjectRequest)
        }
        apiCallerForTV.getTVStreamingLocationJSON(1396) { streamingDetails ->
            Log.e("StreamingDetailsBBBBBBBB", streamingDetails.toString())
        }
    }
    private fun displayTVDetails(tvDetails: JSONObject, streamingDetails: Map<String, String>) {
        tvDetailsObj = tvDetails
        val titleTextView: TextView = findViewById(R.id.TvTitle)
        val overviewTextView: TextView = findViewById(R.id.TvOverview)
        val genreTextView: TextView = findViewById(R.id.TvGenre)
        val posterImageView: ImageView = findViewById(R.id.TvPoster)
        val voteAverageTextView: TextView = findViewById(R.id.TvVoteAverage)
//        val airDateTextView: TextView = findViewById(R.id.TvReleaseDate)
        val voteAverage = tvDetails.getDouble("vote_average")
        val drawableStar = ContextCompat.getDrawable(this, R.drawable.ic_star_vector)

        titleTextView.text = tvDetails.getString("name")
        overviewTextView.text = tvDetails.getString("overview")
//        airDateTextView.text = tvDetails.getString("first_air_date")
        drawableStar?.setBounds(0, 0, drawableStar.intrinsicWidth, drawableStar.intrinsicHeight)
        voteAverageTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStar, null, null, null)
        voteAverageTextView.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.default_padding)
        voteAverageTextView.text = String.format(Locale.getDefault(), "%.1f", voteAverage)

        val genresArray = tvDetails.getJSONArray("genres")
        val genreNames = mutableListOf<String>()
        for (i in 0 until genresArray.length()) {
            val genre = genresArray.getJSONObject(i)
            genreNames.add(genre.getString("name"))
        }
        genreTextView.text = genreNames.joinToString(", ")

        val posterPath = tvDetails.getString("poster_path")
        if (posterPath != "null") {
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            Glide.with(this).load(posterUrl).into(posterImageView)
        } else {
            val posterUrl =  "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.IkbcciGb75wX7U5WeANuDQHaLE%26pid%3DApi&f=1&ipt=a36f8b1fdf094f9245bbbfbf6e0d0908dd49b8c2ae8557f5632a06cce2c36cf2&ipo=images"
            Glide.with(this).load(posterUrl).into(posterImageView)
        }

        // Set the adapter with an empty list initially
        val castRecyclerView: RecyclerView = findViewById(R.id.castRecyclerView)
        val castAdapter = CastAdapter() // No arguments here
        castRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        castRecyclerView.adapter = castAdapter

        // Later, when you have the cast list
        apiCallerForTV.getTVCastDetails(tvDetails.getInt("id")) { castList ->
            runOnUiThread {
                castAdapter.submitList(castList) // Use submitList to update the adapter's data
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