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
//import com.google.gson.Gson
import android.util.Log
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject

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
        //val movieUrl = "https://www.omdbapi.com/?t=batman&apikey=8081b028"
        // Second URL for the search results
        //val searchUrl = "https://www.omdbapi.com/?s=batman&apikey=8081b028"

        val apiUrlsHorror = listOf(
            "https://www.omdbapi.com/?t=five%20nights%20at%20freddy's&apikey=8081b028",
            "https://www.omdbapi.com/?t=the%20boogey&apikey=8081b028",
            "https://www.omdbapi.com/?t=when%20evil%20lurks&apikey=8081b028",
            "https://www.omdbapi.com/?t=nun&apikey=8081b028",
            "https://www.omdbapi.com/?t=no%20one%20will%20save%20you&apikey=8081b028",
            "https://www.omdbapi.com/?t=the%20exorcist%20believer&apikey=8081b028",
            "https://www.omdbapi.com/?t=evil%20dead%20rise&apikey=8081b028",
            )


        val apiUrlsDrama = listOf(
            "https://www.omdbapi.com/?t=the%20creator&apikey=8081b028",
            "https://www.omdbapi.com/?t=Poor%20Things&apikey=8081b028",
            "https://www.omdbapi.com/?t=The%20iron%20claw&apikey=8081b028",
            "https://www.omdbapi.com/?t=oppenheimer&apikey=8081b028",
            "https://www.omdbapi.com/?t=napoleon&apikey=8081b028",
            "https://www.omdbapi.com/?t=The%20boys%20in%20the%20boat&apikey=8081b028",
            "https://www.omdbapi.com/?t=killers%20of%20the%20flower%20moon&apikey=8081b028",
        )
        val apiUrlsAwards = listOf(
            "https://www.omdbapi.com/?t=Everything%20Everywhere%20All%20at%20Once&apikey=8081b028",
            "https://www.omdbapi.com/?t=Avatar:%20The%20Way%20of%20Water&apikey=8081b028",
            "https://www.omdbapi.com/?t=The%20Banshees%20of%20Inisherin&apikey=8081b028",
            "https://www.omdbapi.com/?t=Top%20Gun:%20Maverick&apikey=8081b028",
            "https://www.omdbapi.com/?t=T%C3%A1r&apikey=8081b028",
            "https://www.omdbapi.com/?t=All%20Quiet%20on%20the%20Western%20Front&apikey=8081b028",
            "https://www.omdbapi.com/?t=Elvis&apikey=8081b028",
        )
        mRequestQueue = Volley.newRequestQueue(this)

        val textViewHorror = findViewById<TextView>(R.id.movieDetailsTextViewHorror)
        val textViewDrama = findViewById<TextView>(R.id.movieDetailsTextViewDrama)
        val textViewAward = findViewById<TextView>(R.id.movieDetailsTextViewAwards)

        getData(apiUrlsHorror, textViewHorror)
        getData(apiUrlsDrama, textViewDrama)
        getData(apiUrlsAwards, textViewAward)

    }

    private fun getData(apiUrlList: List<String>, textView: TextView) {
        val movieDetailsList = mutableListOf<String>()

        for (url in apiUrlList) {
            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    Log.d("API Response", response)
                    parseAndAddToDetails(response, movieDetailsList)
                    updateTextView(movieDetailsList, textView)
                },
                { error ->
                    Log.e("API Error", "Error fetching data: $error")
                }
            )

            mRequestQueue?.add(stringRequest)
        }
    }
    private fun parseAndAddToDetails(response: String, movieDetailsList: MutableList<String>) {
        try {
            val jsonObject = JSONObject(response)
            val title = jsonObject.optString("Title")
            val year = jsonObject.optString("Year")
            val imdbID = jsonObject.optString("imdbID")
            val type = jsonObject.optString("Type")
            val poster = jsonObject.optString("Poster")

            val details = "Title: $title\nYear: $year\nIMDb ID: $imdbID\nType: $type\nPoster: $poster\n\n"
            movieDetailsList.add(details)
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }
    }

    private fun updateTextView(movieDetailsList: MutableList<String>, textView: TextView) {
        val allMovieDetails = movieDetailsList.joinToString(separator = "")
        textView.text = allMovieDetails
    }







    //This works
//    private fun getData(movieUrl: String, searchUrl: String) {
//        // RequestQueue initialized
//        mRequestQueue = Volley.newRequestQueue(this)
//
//        // String Request for movie details
//        val movieRequest = StringRequest(Request.Method.GET, movieUrl,
//            { response ->
//                Log.d("MovieResponse", response)
//                try {
//                    val jsonObject = JSONObject(response)
//                    val ratingsArray = jsonObject.optJSONArray("Ratings")
//                    val ratingsList = mutableListOf<Rating>()
//                    if (ratingsArray != null) {
//                        for (i in 0 until ratingsArray.length()) {
//                            val ratingObject = ratingsArray.getJSONObject(i)
//                            val source = ratingObject.optString("Source")
//                            val value = ratingObject.optString("Value")
//                            val rating = Rating(source, value)
//                            ratingsList.add(rating)
//                        }
//                    }
//                    val movie = Movie(
//                        title = jsonObject.optString("Title"),
//                        year = jsonObject.optString("Year"),
//                        rated = jsonObject.optString("Rated"),
//                        released = jsonObject.optString("Released"),
//                        runtime = jsonObject.optString("Runtime"),
//                        genre = jsonObject.optString("Genre"),
//                        director = jsonObject.optString("Director"),
//                        writer = jsonObject.optString("Writer"),
//                        actors = jsonObject.optString("Actors"),
//                        plot = jsonObject.optString("Plot"),
//                        language = jsonObject.optString("Language"),
//                        country = jsonObject.optString("Country"),
//                        awards = jsonObject.optString("Awards"),
//                        poster = jsonObject.optString("Poster"),
//                        ratings = ratingsList,
//                        metascore = jsonObject.optString("Metascore"),
//                        imdbRating = jsonObject.optString("imdbRating"),
//                        imdbVotes = jsonObject.optString("imdbVotes"),
//                        imdbID = jsonObject.optString("imdbID"),
//                        type = jsonObject.optString("Type"),
//                        dvd = jsonObject.optString("DVD"),
//                        boxOffice = jsonObject.optString("BoxOffice"),
//                        production = jsonObject.optString("Production"),
//                        website = jsonObject.optString("Website"),
//                        response = jsonObject.optString("Response")
//                    )
//                    Log.d("movie", movie.toString())
//                    displayMovieDetails(movie, null)
//                } catch (e: JSONException) {
//                    Log.e("TAG", "Error parsing movie details JSON: $e")
//                }
//            },
//            { error ->
//                Log.i("TAG", "Error fetching movie details: $error")
//            }
//        )
//
//
//        // String Request for search results
//        val searchRequest = StringRequest(Request.Method.GET, searchUrl,
//            { response ->
//                Log.d("SearchResponse", response)
//                try {
//                    val jsonObject = JSONObject(response)
//                    val searchArray = jsonObject.optJSONArray("Search")
//                    val searchItemList = mutableListOf<MovieItem>()
//                    if (searchArray != null) {
//                        for (i in 0 until searchArray.length()) {
//                            val itemObject = searchArray.getJSONObject(i)
//                            val title = itemObject.optString("Title")
//                            val year = itemObject.optString("Year")
//                            val imdbID = itemObject.optString("imdbID")
//                            val type = itemObject.optString("Type")
//                            val poster = itemObject.optString("Poster")
//                            val movieItem = MovieItem(title, year, imdbID, type, poster)
//                            searchItemList.add(movieItem)
//                        }
//                    }
//                    val searchResult = SearchResult(searchItemList)
//                    Log.i("searchResult", searchResult.toString())
//                    displayMovieDetails(null, searchResult)
//                } catch (e: JSONException) {
//                    Log.e("TAG", "Error parsing search results JSON: $e")
//                }
//            },
//            { error ->
//                Log.i("TAG", "Error fetching search results: $error")
//            }
//        )
//
//
//        // Add both requests to the request queue
//        mRequestQueue!!.add(movieRequest)
//        mRequestQueue!!.add(searchRequest)
//    }
//
//
//    private fun displayMovieDetails(movie: Movie?, searchResult: SearchResult?) {
//        val details = StringBuilder()
//        Log.i("Entered search", movie.toString())
//        Log.i("Entered search", searchResult.toString())
//        // Append details for the single movie
//        if (movie != null) {
//            //details.append("Single Movie Details:\n")
//            details.append("Title: ${movie.title}\n")
//            details.append("Year: ${movie.year}\n")
//            details.append("Rated: ${movie.rated}\n")
//            details.append("Released: ${movie.released}\n")
//            details.append("Runtime: ${movie.runtime}\n")
//            details.append("Genre: ${movie.genre}\n")
//            details.append("Director: ${movie.director}\n")
//            details.append("Writer: ${movie.writer}\n")
//            details.append("Actors: ${movie.actors}\n")
//            details.append("Plot: ${movie.plot}\n")
//            details.append("Language: ${movie.language}\n")
//            details.append("Country: ${movie.country}\n")
//            details.append("Awards: ${movie.awards}\n")
//            details.append("Poster: ${movie.poster}\n")
//            details.append("Metascore: ${movie.metascore}\n")
//            details.append("imdbRating: ${movie.imdbRating}\n")
//            details.append("imdbVotes: ${movie.imdbVotes}\n")
//            details.append("imdbID: ${movie.imdbID}\n")
//            details.append("Type: ${movie.type}\n")
//            details.append("DVD: ${movie.dvd}\n")
//            details.append("BoxOffice: ${movie.boxOffice}\n")
//            details.append("Production: ${movie.production}\n")
//            details.append("Website: ${movie.website}\n")
//            details.append("Response: ${movie.response}\n")
//            details.append("\n")
//        }
//
//        // Append details for the search results
//        if (searchResult?.search != null) {
//            details.append("Search Results:\n")
//            for (item in searchResult.search) {
//                details.append("Title: ${item.title}\n")
//                details.append("Year: ${item.year}\n")
//                details.append("imdbID: ${item.imdbID}\n")
//                details.append("Type: ${item.type}\n")
//                details.append("Poster: ${item.poster}\n")
//                details.append("\n")
//            }
//        }
//
//        // Set the text of the TextView to the combined details
//        binding.movieDetailsTextView.text = details.toString()
//
//        //binding.movieDetailsTextView.text = "Test Text"
//
//        // Scroll to the top of the ScrollView
//        binding.movieDetailsTextView.post { binding.movieDetailsTextView.scrollTo(0, 0) }
//    }
//
//
//
//
//
//
//
//
//
//
//
//

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