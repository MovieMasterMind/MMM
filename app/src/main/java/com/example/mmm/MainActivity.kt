package com.example.mmm


//imported classes

import APICaller
import MoviePosterAdapter
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.View
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

//import com.google.gson.Gson
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager

//For the TMDB update
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoviePosterAdapter

    @SuppressLint("CutPasteId")
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


        //template API calls
        val apiUrlsHorror =
            "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=27&sort_by=popularity.desc"
        val apiUrlsDrama =
            "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=28&sort_by=popularity.desc"
        val apiUrlsAction =
            "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=18&sort_by=popularity.desc"
        val apiUrlsComedy =
            "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=35&sort_by=popularity.desc"
        val apiUrlsAward =
            "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&sort_by=vote_average.desc&vote_count.gte=1000"

        //template textViews
        val textViewHorror = findViewById<TextView>(R.id.movieDetailsTextViewHorror)
        val textViewDrama = findViewById<TextView>(R.id.movieDetailsTextViewDrama)
        val textViewAction = findViewById<TextView>(R.id.movieDetailsTextViewAction)
        val textViewComedy = findViewById<TextView>(R.id.movieDetailsTextViewComedy)
        val textViewAward = findViewById<TextView>(R.id.movieDetailsTextViewAwards)


        //template recycleViews
        val recyclerViewHorror: RecyclerView = findViewById(R.id.recyclerViewHorror)
        val recyclerViewDrama: RecyclerView = findViewById(R.id.recyclerViewDrama)
        val recyclerViewAction: RecyclerView = findViewById(R.id.recyclerViewAction)
        val recyclerViewComedy: RecyclerView = findViewById(R.id.recyclerViewComedy)
        val recyclerViewAward: RecyclerView = findViewById(R.id.recyclerViewAward)

        //Call setUpRecyclerView for each url textview and recyclerview
        setUpRecyclerView(apiUrlsHorror, textViewHorror, recyclerViewHorror)
        setUpRecyclerView(apiUrlsDrama, textViewDrama, recyclerViewDrama)
        setUpRecyclerView(apiUrlsAction, textViewAction, recyclerViewAction)
        setUpRecyclerView(apiUrlsComedy, textViewComedy, recyclerViewComedy)
        setUpRecyclerView(apiUrlsAward, textViewAward, recyclerViewAward)


    }

    private fun setUpRecyclerView(apiUrl: String, textView: TextView, recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // Initialize your adapter with empty lists for URLs and IDs
        adapter = MoviePosterAdapter(emptyList(), emptyList())

        // Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

        val apiCaller = APICaller()

        // Perform the API call and handle the results
        apiCaller.getData(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
            // Update the adapter with the fetched data
            adapter = MoviePosterAdapter(posterUrls, movieIds)
            recyclerView.adapter = adapter
        }
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
}

