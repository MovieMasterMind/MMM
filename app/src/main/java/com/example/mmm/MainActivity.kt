package com.example.mmm



import APICaller
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
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

    private val checkboxMap = mapOf<String, CheckBox>(
        "Adventure" to findViewById(R.id.checkboxAdventure),
        "Action" to findViewById(R.id.checkboxAction),
        "Comedy" to findViewById(R.id.checkboxComedy),
        "Drama" to findViewById(R.id.checkboxDrama),
        "Thriller" to findViewById(R.id.checkboxThriller),
        "Horror" to findViewById(R.id.checkboxHorror),
        "Romantic Comedy" to findViewById(R.id.checkboxRomanticComedy),
        "Musical" to findViewById(R.id.checkboxMusical),
        "Documentary" to findViewById(R.id.checkboxDocumentary))

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

//        val applyButton: Button = findViewById(R.id.buttonApply)
//        applyButton.setOnClickListener {
//            val selectedGenres = mutableListOf<String>()
//            for ((genre, checkBox) in checkboxMap) {
//                if (checkBox.isChecked) {
//                    selectedGenres.add(genre)
//                }
//            }
//            // Construct search query based on selected options
//            val query = selectedGenres.joinToString("|")

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

        // Placeholder adapter initialization
        adapter = MoviePosterAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        val apiCaller = APICaller()
//        apiCaller.getMovieStreamingLocationJSON(597) { details ->
//            // Log or display the streaming details
//            Log.e("StreamingDetails", details.toString())
//        }



        //apiCaller.getMovieStreamingLocationJSON(597
        //println("This is print once")
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                // Handle click on filter icon
                val dialogFragment = FilterDialogFragment()
                dialogFragment.show(supportFragmentManager, "FilterDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the main menu and options menu
        menuInflater.inflate(R.menu.main, menu)

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
    }}
