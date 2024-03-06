package com.example.mmm

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.example.mmm.databinding.ActivityMainBinding

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import android.util.Log

class MainActivity : AppCompatActivity() {

    //Adding variables API
    private var mRequestQueue: RequestQueue? = null
//    private var mStringRequest: StringRequest? = null
//    private val movieUrl = "//https://www.omdbapi.com/?t=batman&apikey=8081b028"
//    private val searchUrl = "https://www.omdbapi.com/?t=batman&apikey=8081b028"



    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController) // Injecting the code for network request

//        var apiRequestQueue: RequestQueue? = null

        //Calling getData will get the API data from OMDB using the API, to get the JSON file
        getData()





    }

    //This works
    private fun getData() {
        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this)

        // First URL for the movie details
        val movieUrl = "https://www.omdbapi.com/?t=batman&apikey=8081b028"

        // Second URL for the search results
        val searchUrl = "https://www.omdbapi.com/?s=batman&apikey=8081b028"

        // String Request for movie details
        val movieRequest = StringRequest(Request.Method.GET, movieUrl,
            { response ->
                Log.d("MovieResponse", response)
                val movie = Gson().fromJson(response, Movie::class.java)
                displayMovieDetails(movie, null)
            },
            { error ->
                Log.i("TAG", "Error fetching movie details: $error")
            }
        )

        // String Request for search results
        val searchRequest = StringRequest(Request.Method.GET, searchUrl,
            { response ->
                Log.d("SearchResponse", response)
                val searchResult = Gson().fromJson(response, SearchResult::class.java)
                displayMovieDetails(null, searchResult)
            },
            { error ->
                Log.i("TAG", "Error fetching search results: $error")
            }
        )

        // Add both requests to the request queue
        mRequestQueue!!.add(movieRequest)
        mRequestQueue!!.add(searchRequest)
    }


    private fun displayMovieDetails(movie: Movie?, searchResult: SearchResult?) {
        val details = StringBuilder()

        // Append details for the single movie
        if (movie != null) {
            details.append("Single Movie Details:\n")
            details.append("Title: ${movie.title}\n")
            details.append("Year: ${movie.year}\n")
            details.append("Rated: ${movie.rated}\n")
            details.append("Released: ${movie.released}\n")
            details.append("Runtime: ${movie.runtime}\n")
            details.append("Genre: ${movie.genre}\n")
            details.append("Director: ${movie.director}\n")
            details.append("Writer: ${movie.writer}\n")
            details.append("Actors: ${movie.actors}\n")
            details.append("Plot: ${movie.plot}\n")
            details.append("Language: ${movie.language}\n")
            details.append("Country: ${movie.country}\n")
            details.append("Awards: ${movie.awards}\n")
            details.append("Poster: ${movie.poster}\n")
            details.append("Metascore: ${movie.metascore}\n")
            details.append("imdbRating: ${movie.imdbRating}\n")
            details.append("imdbVotes: ${movie.imdbVotes}\n")
            details.append("imdbID: ${movie.imdbID}\n")
            details.append("Type: ${movie.type}\n")
            details.append("DVD: ${movie.dvd}\n")
            details.append("BoxOffice: ${movie.boxOffice}\n")
            details.append("Production: ${movie.production}\n")
            details.append("Website: ${movie.website}\n")
            details.append("Response: ${movie.response}\n")
            details.append("\n")
        }

        // Append details for the search results
        if (searchResult?.search != null) {
            details.append("Search Results:\n")
            for (item in searchResult.search) {
                details.append("Title: ${item.title}\n")
                details.append("Year: ${item.year}\n")
                details.append("imdbID: ${item.imdbID}\n")
                details.append("Type: ${item.type}\n")
                details.append("Poster: ${item.poster}\n")
                details.append("\n")
            }
        }

        // Set the text of the TextView to the combined details
        binding.movieDetailsTextView.text = details.toString()

        // Scroll to the top of the ScrollView
        binding.movieDetailsTextView.post { binding.movieDetailsTextView.scrollTo(0, 0) }
    }













    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    data class Movie(
        val title: String,
        val year: String,
        val rated: String,
        val released: String,
        val runtime: String,
        val genre: String,
        val director: String,
        val writer: String,
        val actors: String,
        val plot: String,
        val language: String,
        val country: String,
        val awards: String,
        val poster: String,
        val ratings: List<Rating>,
        val metascore: String,
        val imdbRating: String,
        val imdbVotes: String,
        val imdbID: String,
        val type: String,
        val dvd: String,
        val boxOffice: String,
        val production: String,
        val website: String,
        val response: String
    )

    data class Rating(
        val source: String,
        val value: String
    )

    data class SearchResult(
        val search: List<MovieItem>
    )

    data class MovieItem(
        val title: String,
        val year: String,
        val imdbID: String,
        val type: String,
        val poster: String
    )
}