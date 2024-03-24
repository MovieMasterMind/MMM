
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.mmm.R
import org.json.JSONObject

class MovieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getIntExtra("MOVIE_ID", 0)
        if (movieId != 0) {
            fetchMovieDetails(movieId)
        }
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
