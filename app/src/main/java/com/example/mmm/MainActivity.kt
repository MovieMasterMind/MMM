package com.example.mmm



import APICaller
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var searchView: SearchView

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

        adapter = MoviePosterAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        val apiCaller = APICaller()
        apiCaller.getData(apiUrl, textView, recyclerView) { posterUrls, movieIds ->
            runOnUiThread {
                adapter = MoviePosterAdapter(posterUrls, movieIds)
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                val query = searchView.query.toString().trim()

                if (query.isNotEmpty()) {
                    val filterListener = object : FilterDialogFragment.FilterListener {
                        override fun onFiltersApplied(selectedFilters: List<String>) {
                            val intent = Intent(applicationContext, SearchableActivity::class.java)
                            intent.putExtra("QUERY", query as CharSequence)
                            intent.putExtra("FILTERS", selectedFilters.toTypedArray())
                            startActivity(intent)
                        }
                    }
                    val dialogFragment = FilterDialogFragment()
                    dialogFragment.setFilterListener(filterListener)

                    dialogFragment.show(supportFragmentManager, "FilterDialogFragment")
                } else {
                    applyFiltersOnly()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyFiltersOnly() {
        val filterListener = object : FilterDialogFragment.FilterListener {
            override fun onFiltersApplied(selectedFilters: List<String>) {
                val intent = Intent(applicationContext, SearchableActivity::class.java)
                intent.putExtra("FILTERS", selectedFilters.toTypedArray())
                startActivity(intent)
            }
        }
        val dialogFragment = FilterDialogFragment()
        dialogFragment.setFilterListener(filterListener)

        dialogFragment.show(supportFragmentManager, "FilterDialogFragment")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search for movies"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(applicationContext, SearchableActivity::class.java)
                intent.putExtra("QUERY", query)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return true
    }

    @Deprecated("Deprecated in Java")
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
