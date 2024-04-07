package com.example.mmm

import CastMember
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class APICaller {

    private val client = OkHttpClient()

    fun getData(apiUrl: String, textView: TextView, recyclerView: RecyclerView, callback: (List<String>, List<Int>) -> Unit) {
        val request = Request.Builder().url(apiUrl).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Error fetching data: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    Log.d("API Response", responseData)

                    val (posterUrls, movieIds) = parseAndDisplayData(responseData, textView)

                    // Post to the main thread without casting to MainActivity
                    Handler(Looper.getMainLooper()).post {
                        callback(posterUrls, movieIds)
                    }
                }
            }
        })
    }

    private fun parseAndDisplayData(response: String, textView: TextView): Pair<List<String>, List<Int>> {
        val posterUrls = mutableListOf<String>()
        val movieIds = mutableListOf<Int>()

        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            for (i in 0 until resultsArray.length()) {
                val movieObject = resultsArray.getJSONObject(i)
                val id = movieObject.getInt("id")
                val posterPath = movieObject.getString("poster_path")
                val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"

                movieIds.add(id)
                posterUrls.add(posterUrl)
            }
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }

        return Pair(posterUrls, movieIds)
    }

    fun getCastDetails(movieId: Int, callback: (List<CastMember>) -> Unit) {
        val url = "https://api.themoviedb.org/3/movie/$movieId/credits?api_key=1f443a53a6aabe4de284f9c46a17f64c"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Error fetching cast details: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    val castList = mutableListOf<CastMember>()
                    try {
                        val jsonObject = JSONObject(responseData)
                        val castArray = jsonObject.getJSONArray("cast")
                        for (i in 0 until castArray.length()) {
                            val castObject = castArray.getJSONObject(i)
                            val name = castObject.getString("name")
                            val character = castObject.getString("character") // Extract character name
                            val profilePath = castObject.optString("profile_path", "")
                            val imageUrl = if (profilePath.isNotEmpty()) {
                                "https://image.tmdb.org/t/p/w500$profilePath"
                            } else {
                                "https://www.nicepng.com/png/full/73-730154_open-default-profile-picture-png.png" // Placeholder image URL if no profile path is available
                            }
                            castList.add(CastMember(name, character, imageUrl)) // Include character name when creating CastMember
                        }
                        // Post to the main thread
                        Handler(Looper.getMainLooper()).post {
                            callback(castList)
                        }
                    } catch (e: JSONException) {
                        Log.e("JSON Error", "Error parsing JSON: $e")
                    }
                }
            }
        })
    }



    private fun parseCastData(response: String): List<CastMember> {
        val castList = mutableListOf<CastMember>()
        try {
            val jsonObject = JSONObject(response)
            val castArray = jsonObject.getJSONArray("cast")
            for (i in 0 until castArray.length()) {
                val castObject = castArray.getJSONObject(i)
                val name = castObject.getString("name")
                val character = castObject.getString("character") // Get character from the object
                val profilePath = castObject.optString("profile_path", "")
                val imageUrl = "https://image.tmdb.org/t/p/w500$profilePath"
                castList.add(CastMember(name, imageUrl, character)) // Include character in the constructor
            }
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }
        return castList
    }


    private fun getPosterUrls(resultsArray: JSONArray): List<String> {
        val posterUrls = mutableListOf<String>()
        for (i in 0 until resultsArray.length()) {
            val movieObject = resultsArray.getJSONObject(i)
            val posterPath = movieObject.optString("poster_path")
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            posterUrls.add(posterUrl)
        }
        return posterUrls


    }

    fun getMovieStreamingLocationJSON(tmdbId: Int, callback: (Map<String, String>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()

            val url = "https://streaming-availability.p.rapidapi.com/get?output_language=en&tmdb_id=movie%2F$tmdbId"
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", "24562cc0e2msh9d6623953b461fdp18b00ejsna654dc783352")
                .addHeader("X-RapidAPI-Host", "streaming-availability.p.rapidapi.com")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val streamingDetails = parseStreamingInfo(responseBody)
                    callback(streamingDetails)
                } else {
                    Log.e("APICaller", "Request failed with code: ${response.code}")
                    Log.e("APICaller", "Request failed with tmdbId: $tmdbId")
                    // Return an empty map as an empty response
                    callback(emptyMap())
                }
            } catch (e: IOException) {
                Log.e("APICaller", "Exception: ${e.message}")
                // Return an empty map as an empty response
                callback(emptyMap())
            }
        }
    }



    private fun parseStreamingInfo(responseBody: String?): Map<String, String> {
        val streamingDetails = mutableMapOf<String, String>()
        responseBody?.let {
            val jsonObject = JSONObject(it)
            val resultObject = jsonObject.optJSONObject("result")
            val streamingInfoObject = resultObject?.optJSONObject("streamingInfo")
            streamingInfoObject?.let {
                val usStreamingArray = streamingInfoObject.optJSONArray("us")
                usStreamingArray?.let {
                    for (i in 0 until usStreamingArray.length()) {
                        val usStreamingDetail = usStreamingArray.getJSONObject(i)
                        val service = usStreamingDetail.getString("service")
                        val streamingType = usStreamingDetail.getString("streamingType")
                        val link = usStreamingDetail.getString("link")
                        streamingDetails[service] = link
                    }
                }
            }
        }
        return streamingDetails
    }
}