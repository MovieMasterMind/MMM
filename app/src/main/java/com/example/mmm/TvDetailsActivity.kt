package com.example.mmm

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                displayTVDetails(response)
            },
            { error ->
                Log.e("TVDetailsActivity", "Error fetching tv details: $error")
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
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

    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }

}