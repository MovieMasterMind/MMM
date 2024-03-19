package com.example.mmm

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class APICaller {
    //This is used for the client.NewCall
    private val client = OkHttpClient()

    fun getData(apiUrlList: List<String>, textView: TextView, imageView: ImageView) {


        val movieDetailsList = mutableListOf<String>()

        for (url in apiUrlList) {
            val request = Request.Builder().url(url).get().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("API Error", "Error fetching data: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let {
                        val responseData = it.string()
                        Log.d("API Response", responseData)
                        parseAndAddToDetails(responseData, movieDetailsList, imageView)
                        updateTextView(movieDetailsList, textView)
                    }
                }
            })
        }
    }

    private fun parseAndAddToDetails(response: String, movieDetailsList: MutableList<String>, imageView: ImageView) {
        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            for (i in 0 until resultsArray.length()) {
                val movieObject = resultsArray.getJSONObject(i)
                val title = movieObject.optString("title")
                val releaseDate = movieObject.optString("release_date")
                val overview = movieObject.optString("overview")
                val posterPath = movieObject.optString("poster_path")

                val details = "Title: $title\nRelease Date: $releaseDate\nOverview: $overview\nPoster: $posterPath\n\n"
                movieDetailsList.add(details)

                // Load poster image using Glide
                val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath" // Construct poster URL

                //TODO get working with RecyclerView as right now this just Crashes on start up
                //Glide.with(imageView.context).load(posterUrl).into(imageView)
            }
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }
    }

    private fun updateTextView(movieDetailsList: MutableList<String>, textView: TextView) {
        val allMovieDetails = movieDetailsList.joinToString(separator = "")
        textView.text = allMovieDetails
    }

}