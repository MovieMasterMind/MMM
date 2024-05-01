package com.example.mmm

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.APICallerForTV
import org.w3c.dom.Text
import java.util.Locale

class EpisodeDetailActivity : AppCompatActivity() {

    private val apiCallerForTV = APICallerForTV()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        // Retrieve the data from the intent
        intent?.extras?.let {
            val tvShowId = it.getInt("tvID", -1)
            val seasonId = it.getInt("seasonNum", -1)
            val episodeNumber = it.getString("episode_num", "N/A")
            val posterPath = it.getString("imageURL", "")
            val overview = it.getString("overview", "")
            val voteAverage = it.getDouble("voteAverage", -0.1)
            val name = it.getString("name", "")
            var runTime = it.getString("runtime", "").toInt()

        val posterImageView: ImageView = findViewById(R.id.episodePoster)
//        var tvShowIdView: TextView = findViewById(R.id.tvShowId)
//        var seasonIdView: TextView = findViewById(R.id.seasonId)
        var episodeNumberView: TextView = findViewById(R.id.episodeNumber)
        var episodeOverviewView: TextView = findViewById(R.id.episodeOverview)

        val episodeVoteAverage: TextView = findViewById(R.id.episodeVoteAverage)
        val drawableStar = ContextCompat.getDrawable(this, R.drawable.ic_star_vector)
        drawableStar?.setBounds(0, 0, drawableStar.intrinsicWidth, drawableStar.intrinsicHeight)
        episodeVoteAverage.setCompoundDrawablesWithIntrinsicBounds(drawableStar, null, null, null)
        episodeVoteAverage.compoundDrawablePadding =
            resources.getDimensionPixelSize(R.dimen.default_padding)
        episodeVoteAverage.text = String.format(Locale.getDefault(), "%.1f", voteAverage)
            var episodeRuntime: TextView = findViewById(R.id.episodeRuntime)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_episode_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



            // Display the data
//            tvShowIdView.text = "$tvShowId"
//            seasonIdView.text = "$seasonId"
            episodeNumberView.text = "S$seasonId E$episodeNumber"
            episodeOverviewView.text = "$overview"
            var hours = 0
            while (runTime > 60) {
                runTime = runTime - 60
                hours = hours + 1
            }
            if (hours > 0) {
                episodeRuntime.text = getString(R.string.runtime, hours, runTime)
            } else {episodeRuntime.text = getString(R.string.runtime_nohrs, runTime)}

            if (posterPath != "null") {
                val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
                Glide.with(this).load(posterUrl).into(posterImageView)
            } else {
                val posterUrl =
                    "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.IkbcciGb75wX7U5WeANuDQHaLE%26pid%3DApi&f=1&ipt=a36f8b1fdf094f9245bbbfbf6e0d0908dd49b8c2ae8557f5632a06cce2c36cf2&ipo=images"
                Glide.with(this).load(posterUrl).into(posterImageView)
            }


            val castRecyclerView: RecyclerView = findViewById(R.id.castRecyclerView)
            val castAdapter = CastAdapter() // No arguments here
            castRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            castRecyclerView.adapter = castAdapter

            val combinedCastList = mutableListOf<CastMember>()

            // Call for TV show cast details
            apiCallerForTV.getTVCastDetails(tvShowId) { castList ->
                combinedCastList.addAll(castList)

                // Inside the first API call, make the second API call
                apiCallerForTV.getTVEpisodeCast(tvShowId, seasonId.toString(), episodeNumber) { episodeCastList ->
                    combinedCastList.addAll(episodeCastList)
                    runOnUiThread {
                        castAdapter.submitList(combinedCastList.toList()) // Convert to list to ensure immutability
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close this activity and return to the previous one
        return true
    }
}
