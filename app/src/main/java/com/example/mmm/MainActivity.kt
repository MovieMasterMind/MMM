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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import android.util.Log;
import android.widget.Toast;

class MainActivity : AppCompatActivity() {

    //Adding API?

    private var mRequestQueue: RequestQueue? = null
    private var mStringRequest: StringRequest? = null
    private val url = "https://www.omdbapi.com/?t=batman&apikey=8081b028"


    //End of API

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

        var apiRequestQueue: RequestQueue? = null
        getData()




        //this didn't work
        val apiUrl = "https://jsonplaceholder.typicode.com/posts/2" // Example API endpoint
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val (_, response, result) = Fuel.get(apiUrl)
                    .timeout(10000) // Set timeout to 10 seconds
                    .responseObject<Post>()

                if (response.statusCode == 200) {
                    val (post, _) = result
                    if (post != null) {
                        println("Post ID: ${post.id}")
                        println("User ID: ${post.userId}")
                        println("Title: ${post.title}")
                        println("Body: ${post.body}")
                    } else {
                        println("No post found")
                    }
                } else {
                    println("Error: ${response.statusCode} - ${response.responseMessage} End")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
    private fun getData() {
        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this)

        // String Request initialized
        mStringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // display the response on screen
                Toast.makeText(applicationContext, "Response: $response", Toast.LENGTH_LONG).show()
                Log.i("TEST", "Response: $response")
            },
            Response.ErrorListener { error ->
                Log.i("TAG", "Error: ${error.toString()}")
            }
        )
        mRequestQueue!!.add(mStringRequest)
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


    data class Post(
        val userId: Int,
        val id: Int,
        val title: String,
        val body: String
    )
}