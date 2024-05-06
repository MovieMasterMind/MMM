package com.example.mmm

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
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
    private var tvShowId = 2
    private var selectedSeasonButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("CREATING TV DETAILS", "CREATING TV DETAILS")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_tv_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        Log.d("GETTING TV ID", "GETTING TV ID")

        val tvId = intent.getIntExtra("TV_ID", -1)
        if (tvId != -1) {
            Log.d("fetchStreamingDetails CALLED", "fetchStreamingDetails CALLED")
            fetchStreamingDetails(tvId)
            fetchTVDetails(tvId)
            displaySeasonsList(tvId)
            fetchAndDisplayTrailers(tvId)
            tvShowId = tvId
        } else {
            finish() // Close the activity if tv ID wasn't passed correctly
        }
    }

    private fun fetchAndDisplayTrailers(tvId: Int) {
        apiCallerForTV.getTVTrailers(tvId) { trailers ->
            val recyclerView: RecyclerView = findViewById(R.id.trailerRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = TrailerAdapter(trailers)
        }
    }


    private fun getYouTubeHTML(embedURL: String): String {
        Log.d("got embed", embedURL)
        return "<iframe width=\"100%\" height=\"100%\" src=\"$embedURL\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
    }


    private fun fetchTVDetails(tvId: Int) {
        val url =
            "https://api.themoviedb.org/3/tv/$tvId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"


            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    // Existing code to display movie details
                    displayTVDetails(response)
                },
                { error ->
                    Log.e("TVDetailsActivity", "Error fetching movie details: $error")
                }
            )

            Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun fetchStreamingDetails(tvId: Int) {
        val url =
            "https://api.themoviedb.org/3/tv/$tvId?api_key=1f443a53a6aabe4de284f9c46a17f64c&language=en-US"

        apiCallerForTV.getTVStreamingLocationJSON(tvId) { streamingDetails ->
            Log.e("StreamingDetailsForTV", streamingDetails.toString())

            val streamingDetailsFound = streamingDetails.isNotEmpty()
            val textViewText = if (streamingDetailsFound) {
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
                    displayStreamingLayout(streamingDetails)
                },
                { error ->
                    Log.e("TVDetailsActivity", "Error fetching movie details: $error")
                }
            )

            Volley.newRequestQueue(this).add(jsonObjectRequest)
        }
    }
    private fun displayTVDetails(tvDetails: JSONObject) {
        tvDetailsObj = tvDetails
        val titleTextView: TextView = findViewById(R.id.TvTitle)
        val overviewTextView: TextView = findViewById(R.id.TvOverview)
        val genreTextView: TextView = findViewById(R.id.TvGenre)
        val posterImageView: ImageView = findViewById(R.id.TvPoster)
        val voteAverageTextView: TextView = findViewById(R.id.TvVoteAverage)
//        val airDateTextView: TextView = findViewById(R.id.TvReleaseDate)
        val voteAverage = tvDetails.getDouble("vote_average")
        val drawableStar = ContextCompat.getDrawable(this, R.drawable.ic_star_vector)

        overviewTextView.setOnClickListener {
            if (overviewTextView.maxLines == 4) {
                overviewTextView.maxLines = Int.MAX_VALUE // Expand the TextView
            } else {
                overviewTextView.maxLines = 4 // Collapse the TextView
            }
        }

        // Display year range
        val firstAirDate = tvDetails.getString("first_air_date")
        val lastAirDate = tvDetails.getString("last_air_date")
        val status = tvDetails.getString("status")

        val yearRange = if (status.equals("Returning Series", ignoreCase = true)) {
            // Show "Present" if the show is a returning series
            "${firstAirDate.substring(0, 4)} - Present"
        } else {
            // Calculate year range if the show has ended
            val endYear = if (lastAirDate != "null") lastAirDate.substring(0, 4) else "Present"
            "${firstAirDate.substring(0, 4)} - $endYear"
        }
        Log.d("year range", yearRange)
        val title = tvDetails.getString("name")
        titleTextView.text = "$title ($yearRange)"
        if (tvDetails.getString("overview") != "") {
        overviewTextView.text = tvDetails.getString("overview")}
//        airDateTextView.text = tvDetails.getString("first_air_date")
        drawableStar?.setBounds(0, 0, drawableStar.intrinsicWidth, drawableStar.intrinsicHeight)
        voteAverageTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStar, null, null, null)
        voteAverageTextView.compoundDrawablePadding =
            resources.getDimensionPixelSize(R.dimen.default_padding)
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
            val posterUrl =
                "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.IkbcciGb75wX7U5WeANuDQHaLE%26pid%3DApi&f=1&ipt=a36f8b1fdf094f9245bbbfbf6e0d0908dd49b8c2ae8557f5632a06cce2c36cf2&ipo=images"
            Glide.with(this).load(posterUrl).into(posterImageView)
        }

        // Set the adapter with an empty list initially
        val castRecyclerView: RecyclerView = findViewById(R.id.castRecyclerView)
        val castAdapter = CastAdapter() // No arguments here
        castRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        castRecyclerView.adapter = castAdapter

        // Later, when you have the cast list
        apiCallerForTV.getAggregateTVCastDetails(tvDetails.getInt("id")) { castList ->
            runOnUiThread {
                castAdapter.submitList(castList) // Use submitList to update the adapter's data
            }
        }
        apiCallerForTV.getContentRatings(tvDetails.getInt("id")) { rating ->
            runOnUiThread {
                // Update the UI with the content rating
                val contentRatingTextView: TextView = findViewById(R.id.TvContentRating)
                contentRatingTextView.text = rating
            }
        }
    }


        private fun displayStreamingLayout(streamingDetails: Map<String, String>) {

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

    private fun displaySeasonsList(tvId: Int) {
        apiCallerForTV.getTVSeasonsNames(tvId) { seasonsNamesList ->
            updateSeasonsUI(seasonsNamesList)
        }
    }

    private fun updateSeasonsUI(seasons: List<String>) {
        runOnUiThread {
            val seasonsContainer: LinearLayout = findViewById(R.id.seasonsContainer)
            seasonsContainer.removeAllViews()

            val seasonPairs = seasons.windowed(size = 2, step = 2, partialWindows = false)
            val (specials, regularSeasons) = seasonPairs.partition { it[1] == "0" }
            val sortedSeasons = regularSeasons.sortedBy { it[1].toInt() } + specials

            sortedSeasons.forEachIndexed { index, pair ->
                val (seasonName, seasonNumber) = pair
                val buttonText = if (seasonNumber == "0") seasonName else "Season $seasonNumber"

                val seasonButton = Button(this).apply {
                    text = buttonText
                    setBackgroundColor(Color.BLACK)
                    setTextColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                    setOnClickListener {
                        selectedSeasonButton?.let {
                            it.setBackgroundColor(Color.BLACK)  // Reset the old selected button
                            it.setTextColor(Color.WHITE)
                        }
                        setBackgroundColor(Color.WHITE)  // Highlight the new selected button
                        setTextColor(Color.BLACK)
                        selectedSeasonButton = this  // Update the reference to the new button
                        fetchAndDisplaySeasonData(seasonNumber.toInt())
                    }
                }

                seasonsContainer.addView(seasonButton)
                if (index == 0 && selectedSeasonButton == null) {
                    selectedSeasonButton = seasonButton
                    seasonButton.performClick()  // Automatically click the first season button
                }
            }
        }
    }

    private fun fetchAndDisplaySeasonData(seasonNumber: Int) {
        val tvId = tvShowId
        apiCallerForTV.getTVSeasonsData(tvId, seasonNumber.toString()) { episodeList ->
            Log.d("TvDetailsActivity", "Episodes fetched: ${episodeList.size}")
            val episodeDetails = episodeList.map { episode ->
                EpisodeDetail(
                    episodeNumber = "${episode.episode_number}. ${episode.name}",
                    episodeName = episode.name,
                    episodeOverview = episode.overview,
                    imageUrl = "https://image.tmdb.org/t/p/w500${episode.still_path}",
                    voteAverage = episode.vote_average,
                    IdForTVShow = tvId,
                    SeasonNum = seasonNumber,
                    runTime = episode.runtime,
                )
            }
            Log.d("TvDetailsActivity", "Mapping done, updating UI with ${episodeDetails.size} items.")
            updateEpisodesUI(episodeDetails)
        }
    }

    private fun updateEpisodesUI(episodeDetails: List<EpisodeDetail>) {
        Log.d("TvDetailsActivity", "Updating episodes UI.")
        val episodesRecyclerView: RecyclerView = findViewById(R.id.episodesRecyclerView)
        if (episodesRecyclerView.adapter == null) {
            episodesRecyclerView.layoutManager = LinearLayoutManager(this)
            episodesRecyclerView.adapter = EpisodesAdapter(episodeDetails)
            Log.d("TvDetailsActivity", "Adapter set with new data.")
        } else {
            (episodesRecyclerView.adapter as EpisodesAdapter).updateEpisodes(episodeDetails)
            Log.d("TvDetailsActivity", "Adapter updated with new data.")
        }
    }


    private fun getColorForService(service: String): Pair<Int, Int>? {
        return serviceColorsMap[service.lowercase(Locale.ROOT)]
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

}