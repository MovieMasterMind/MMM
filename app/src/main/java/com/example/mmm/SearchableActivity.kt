package com.example.mmm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu

class SearchableActivity : AppCompatActivity() {

//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding
    private lateinit var queryTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.appBarMain.toolbar)
//
//        val drawerLayout: DrawerLayout = binding.drawerLayout
//        val navView: NavigationView = binding.navView
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController) // Injecting the code for network request


        queryTextView = findViewById(R.id.queryTextView)

        val previousQuery = getPreviousSearchQuery()
        displaySearchQuery(previousQuery)
    }

    // Function to retrieve the previous search query
    private fun getPreviousSearchQuery(): String {
        // For demonstration, assume the previous search query is retrieved from SharedPreferences
        val sharedPreferences = getSharedPreferences("search_preferences", MODE_PRIVATE)
        val previousQuery = sharedPreferences.getString("previous_query", "") ?: ""
        Log.d("SearchableActivity", "Previous search query: $previousQuery")

        return previousQuery
    }

    // Function to display the search query on the screen
    private fun displaySearchQuery(query: String) {
        Log.d("SearchableActivity", "Formatted string: $query")
        val formattedString = getString(R.string.previous_search_query, query)
        queryTextView.text = formattedString
    }

    // Display search bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
