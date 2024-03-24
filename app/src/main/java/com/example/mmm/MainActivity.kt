package com.example.mmm


//imported classes

//For the TMDB update

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmm.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MoviePosterAdapter

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

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
        // Set up layout manager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        // Create an instance of the adapter
        adapter = MoviePosterAdapter(emptyList())
        // Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

        val apiCaller = APICaller() // Create an instance of APICaller

        apiCaller.getData(apiUrl, textView, recyclerView)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Removed settings button temporarily since it may cause the search
        // function to disappear if clicked while in search bar
        menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search for movies"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Called when the user submits final query
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Navigate to the search_results activity
                val intent = Intent(applicationContext, SearchableActivity::class.java)
                intent.putExtra("QUERY", query)
                startActivity(intent)

                return true
            }

            // Called everytime a character is changed in the query
            override fun onQueryTextChange(newText: String?): Boolean {
                // Not needed for this case
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

