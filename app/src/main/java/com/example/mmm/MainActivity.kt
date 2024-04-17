package com.example.mmm



import android.content.Intent
import APICaller
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmm.databinding.ActivityMainBinding
import android.util.Log
import androidx.core.view.GravityCompat

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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_watchlist -> {
                    val intent = Intent(this, WatchlistActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }


        //template API calls
        val apiUrlsHorror = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=27&sort_by=popularity.desc"
        val apiUrlsDrama = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=28&sort_by=popularity.desc"
        val apiUrlsAction = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=18&sort_by=popularity.desc"
        val apiUrlsComedy = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=35&sort_by=popularity.desc"
        val apiUrlsAward = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&sort_by=vote_average.desc&vote_count.gte=1000"
        val apiUrlsAdventure = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=12&sort_by=popularity.desc"
        val apiUrlsAnimation = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=16&sort_by=popularity.desc"
        val apiUrlsCrime = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=80&sort_by=popularity.desc"
        val apiUrlsDocumentary = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=99&sort_by=popularity.desc"
        val apiUrlsFamily = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=10751&sort_by=popularity.desc"
        val apiUrlsFantasy = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=14&sort_by=popularity.desc"
        val apiUrlsHistory = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=36&sort_by=popularity.desc"
        val apiUrlsMusic = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=10402&sort_by=popularity.desc"
        val apiUrlsMystery = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=9648&sort_by=popularity.desc"
        val apiUrlsRomance = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=10749&sort_by=popularity.desc"
        val apiUrlsScienceFiction = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=878&sort_by=popularity.desc"
        val apiUrlsTVMovie = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=10770&sort_by=popularity.desc"
        val apiUrlsThriller = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=53&sort_by=popularity.desc"
        val apiUrlsWar = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=10752&sort_by=popularity.desc"
        val apiUrlsWestern = "https://api.themoviedb.org/3/discover/movie?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=37&sort_by=popularity.desc"


        // Declare variables for TextViews
        val textViewAction = findViewById<TextView>(R.id.movieDetailsTextViewAction)
        val textViewAdventure = findViewById<TextView>(R.id.movieDetailsTextViewAdventure)
        val textViewAnimation = findViewById<TextView>(R.id.movieDetailsTextViewAnimation)
        val textViewComedy = findViewById<TextView>(R.id.movieDetailsTextViewComedy)
        val textViewCrime = findViewById<TextView>(R.id.movieDetailsTextViewCrime)
        val textViewDocumentary = findViewById<TextView>(R.id.movieDetailsTextViewDocumentary)
        val textViewDrama = findViewById<TextView>(R.id.movieDetailsTextViewDrama)
        val textViewFamily = findViewById<TextView>(R.id.movieDetailsTextViewFamily)
        val textViewFantasy = findViewById<TextView>(R.id.movieDetailsTextViewFantasy)
        val textViewHistory = findViewById<TextView>(R.id.movieDetailsTextViewHistory)
        val textViewHorror = findViewById<TextView>(R.id.movieDetailsTextViewHorror)
        val textViewMusic = findViewById<TextView>(R.id.movieDetailsTextViewMusic)
        val textViewMystery = findViewById<TextView>(R.id.movieDetailsTextViewMystery)
        val textViewRomance = findViewById<TextView>(R.id.movieDetailsTextViewRomance)
        val textViewScienceFiction = findViewById<TextView>(R.id.movieDetailsTextViewScienceFiction)
        val textViewTVMovie = findViewById<TextView>(R.id.movieDetailsTextViewTVMovie)
        val textViewThriller = findViewById<TextView>(R.id.movieDetailsTextViewThriller)
        val textViewWar = findViewById<TextView>(R.id.movieDetailsTextViewWar)
        val textViewWestern = findViewById<TextView>(R.id.movieDetailsTextViewWestern)

        // Declare variables for RecyclerViews
        val recyclerViewAction: RecyclerView = findViewById(R.id.recyclerViewAction)
        val recyclerViewAdventure: RecyclerView = findViewById(R.id.recyclerViewAdventure)
        val recyclerViewAnimation: RecyclerView = findViewById(R.id.recyclerViewAnimation)
        val recyclerViewComedy: RecyclerView = findViewById(R.id.recyclerViewComedy)
        val recyclerViewCrime: RecyclerView = findViewById(R.id.recyclerViewCrime)
        val recyclerViewDocumentary: RecyclerView = findViewById(R.id.recyclerViewDocumentary)
        val recyclerViewDrama: RecyclerView = findViewById(R.id.recyclerViewDrama)
        val recyclerViewFamily: RecyclerView = findViewById(R.id.recyclerViewFamily)
        val recyclerViewFantasy: RecyclerView = findViewById(R.id.recyclerViewFantasy)
        val recyclerViewHistory: RecyclerView = findViewById(R.id.recyclerViewHistory)
        val recyclerViewHorror: RecyclerView = findViewById(R.id.recyclerViewHorror)
        val recyclerViewMusic: RecyclerView = findViewById(R.id.recyclerViewMusic)
        val recyclerViewMystery: RecyclerView = findViewById(R.id.recyclerViewMystery)
        val recyclerViewRomance: RecyclerView = findViewById(R.id.recyclerViewRomance)
        val recyclerViewScienceFiction: RecyclerView = findViewById(R.id.recyclerViewScienceFiction)
        val recyclerViewTVMovie: RecyclerView = findViewById(R.id.recyclerViewTVMovie)
        val recyclerViewThriller: RecyclerView = findViewById(R.id.recyclerViewThriller)
        val recyclerViewWar: RecyclerView = findViewById(R.id.recyclerViewWar)
        val recyclerViewWestern: RecyclerView = findViewById(R.id.recyclerViewWestern)

        // Function calls
        setUpRecyclerView(apiUrlsAction, textViewAction, recyclerViewAction)
        setUpRecyclerView(apiUrlsAdventure, textViewAdventure, recyclerViewAdventure)
        setUpRecyclerView(apiUrlsAnimation, textViewAnimation, recyclerViewAnimation)
        setUpRecyclerView(apiUrlsComedy, textViewComedy, recyclerViewComedy)
        setUpRecyclerView(apiUrlsCrime, textViewCrime, recyclerViewCrime)
        setUpRecyclerView(apiUrlsDocumentary, textViewDocumentary, recyclerViewDocumentary)
        setUpRecyclerView(apiUrlsDrama, textViewDrama, recyclerViewDrama)
        setUpRecyclerView(apiUrlsFamily, textViewFamily, recyclerViewFamily)
        setUpRecyclerView(apiUrlsFantasy, textViewFantasy, recyclerViewFantasy)
        setUpRecyclerView(apiUrlsHistory, textViewHistory, recyclerViewHistory)
        setUpRecyclerView(apiUrlsHorror, textViewHorror, recyclerViewHorror)
        setUpRecyclerView(apiUrlsMusic, textViewMusic, recyclerViewMusic)
        setUpRecyclerView(apiUrlsMystery, textViewMystery, recyclerViewMystery)
        setUpRecyclerView(apiUrlsRomance, textViewRomance, recyclerViewRomance)
        setUpRecyclerView(apiUrlsScienceFiction, textViewScienceFiction, recyclerViewScienceFiction)
        setUpRecyclerView(apiUrlsTVMovie, textViewTVMovie, recyclerViewTVMovie)
        setUpRecyclerView(apiUrlsThriller, textViewThriller, recyclerViewThriller)
        setUpRecyclerView(apiUrlsWar, textViewWar, recyclerViewWar)
        setUpRecyclerView(apiUrlsWestern, textViewWestern, recyclerViewWestern)




//
//        //template textViews
//        val textViewHorror = findViewById<TextView>(R.id.movieDetailsTextViewHorror)
//        val textViewDrama = findViewById<TextView>(R.id.movieDetailsTextViewDrama)
//        val textViewAction = findViewById<TextView>(R.id.movieDetailsTextViewAction)
//        val textViewComedy = findViewById<TextView>(R.id.movieDetailsTextViewComedy)
//        val textViewAward = findViewById<TextView>(R.id.movieDetailsTextViewAwards)
//
//
//        //template recycleViews
//        val recyclerViewHorror: RecyclerView = findViewById(R.id.recyclerViewHorror)
//        val recyclerViewDrama: RecyclerView = findViewById(R.id.recyclerViewDrama)
//        val recyclerViewAction: RecyclerView = findViewById(R.id.recyclerViewAction)
//        val recyclerViewComedy: RecyclerView = findViewById(R.id.recyclerViewComedy)
//        val recyclerViewAward: RecyclerView = findViewById(R.id.recyclerViewAward)
//
//        //Call setUpRecyclerView for each url textview and recyclerview
//        setUpRecyclerView(apiUrlsHorror, textViewHorror, recyclerViewHorror)
//        setUpRecyclerView(apiUrlsDrama, textViewDrama, recyclerViewDrama)
//        setUpRecyclerView(apiUrlsAction, textViewAction, recyclerViewAction)
//        setUpRecyclerView(apiUrlsComedy, textViewComedy, recyclerViewComedy)
//        setUpRecyclerView(apiUrlsAward, textViewAward, recyclerViewAward)


    }

    private fun setUpRecyclerView(apiUrl: String, textView: TextView, recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // Placeholder adapter initialization
        adapter = MoviePosterAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        val apiCaller = APICaller()
//        apiCaller.getMovieStreamingLocationJSON(597) { details ->
//            // Log or display the streaming details
//            Log.e("StreamingDetails", details.toString())
//        }



        //apiCaller.getMovieStreamingLocationJSON(597
        //println("This is print onces")
        // Get data from API and update the adapter
        apiCaller.getData(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
            // Run on UI thread since response callback is on a background thread
            runOnUiThread {
                // Create a new adapter with the data
                adapter = MoviePosterAdapter(posterUrls, movieIds)
                recyclerView.adapter = adapter
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the main menu and options menu
        menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.options_menu, menu)

        // Find the search item in the menu
        val searchItem = menu.findItem(R.id.search)
        // Extract the SearchView from the search item
        val searchView = searchItem.actionView as SearchView

        // Set an empty query and a hint for the search view
        searchView.setQuery("", false)

        searchView.queryHint = "Search for movies"

        // Set up a listener for query text changes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Called when the user submits final query
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Navigate to the SearchableActivity with the query
                val intent = Intent(applicationContext, SearchableActivity::class.java)
                intent.putExtra("QUERY", query)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }

            // Called when the query text is changed by the user
            override fun onQueryTextChange(newText: String?): Boolean {
                // You can perform actions based on text changes here if needed
                return true
            }
        })

        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            updateNavigationSelection()
        }
    }

    private fun updateNavigationSelection() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        when (navController.currentDestination?.id) {
            R.id.nav_home -> binding.navView.setCheckedItem(R.id.nav_home)
            R.id.nav_gallery -> binding.navView.setCheckedItem(R.id.nav_gallery)
            R.id.nav_slideshow -> binding.navView.setCheckedItem(R.id.nav_slideshow)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val upNavigated = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        if (upNavigated) {
            updateNavigationSelection()
        }
        return upNavigated
    }
}
