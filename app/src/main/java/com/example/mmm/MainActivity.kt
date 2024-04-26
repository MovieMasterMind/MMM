package com.example.mmm



import android.content.Intent
import APICallerForMovie
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import android.view.MenuItem
import android.widget.CheckBox
import androidx.core.view.GravityCompat


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoviePosterAdapter
    private var checkboxMap: MutableMap<String, CheckBox?> = mutableMapOf()

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








        //TEST CODE FOR OTHERS TO UNDERSTAND HOW TO USE THEM (CAN BE DELETED)
        val apiCallerForMovie = APICallerForMovie()
        val apiCallerForTV = APICallerForTV()
        val movieId = 603
        val TVID = 1433

        //HOW TO USE getMovieTrailers
        apiCallerForMovie.getMovieTrailers(movieId) { trailerList ->
            // Log all YouTube URLs
            trailerList.forEachIndexed { index, trailerMember ->
                Log.d("YouTube URL FROM getMovieTrailers$index", trailerMember.YouTubeURL)
            }
        }

        //EXAMPLE USE OF TV SHOWS
        val apiUrlsComedyTV = "https://api.themoviedb.org/3/discover/tv?api_key=1f443a53a6aabe4de284f9c46a17f64c&with_genres=35&sort_by=popularity.desc"
        val textViewComedyTV = findViewById<TextView>(R.id.movieDetailsTextViewComedyTV)
        val recyclerViewComedyTV: RecyclerView = findViewById(R.id.recyclerViewComedyTV)
        setUpRecyclerView(apiUrlsComedyTV, textViewComedyTV, recyclerViewComedyTV)



        apiCallerForTV.getTVTrailers(TVID) { trailerList ->
            // Log all YouTube URLs
            trailerList.forEachIndexed { index, trailerMember ->
                Log.d("YouTube URL FROM getTVTrailers$index", trailerMember.YouTubeURL)
            }
        }
        //END OF TEST CODE

    }

    private fun setUpRecyclerView(apiUrl: String, textView: TextView, recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // Placeholder adapter initialization
        adapter = MoviePosterAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        val apiCallerForMovie = APICallerForMovie()
        val apiCallerForTV = APICallerForTV()


        if (apiUrl.contains("movie")) {

            // Get data from API and update the adapter
            apiCallerForMovie.getMovieDataFromAPI(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
                // Run on UI thread since response callback is on a background thread
                runOnUiThread {
                    // Create a new adapter with the data
                    adapter = MoviePosterAdapter(posterUrls, movieIds)
                    recyclerView.adapter = adapter
                }
            }


        } else if (apiUrl.contains("tv")) {


            // Get data from API and update the adapter
            apiCallerForTV.getTVDataFromAPI(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
                // Run on UI thread since response callback is on a background thread
                runOnUiThread {
                    // Create a new adapter with the data
                    adapter = MoviePosterAdapter(posterUrls, movieIds)
                    recyclerView.adapter = adapter
                }
            }


        } else {
            Log.e("UNKNOWN URL", "UNKNOWN URL")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Immediately start SearchableActivity without waiting for user input
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(Intent(this@MainActivity, SearchableActivity::class.java))
                return false // Prevents the SearchView from expanding
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Handle any cleanup if needed when search view is collapsed
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