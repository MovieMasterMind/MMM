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
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
class APICallerForTV {

    private val client = OkHttpClient()

    fun fetchTVDataFromAPI(
        apiUrl: String,
        processJson: (String) -> Triple<List<String>, List<String>, List<String>>,
        callback: (List<String>, List<String>, List<String>) -> Unit
    ) {
        val request = Request.Builder().url(apiUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Failed to fetch TV data: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    val result = processJson(responseData)
                    Handler(Looper.getMainLooper()).post {
                        callback(result.first, result.second, result.third)
                    }
                }
            }
        })
    }

    fun getTVDataFromAPI(apiUrl: String, textView: TextView, recyclerView: RecyclerView, callback: (List<String>, List<Int>) -> Unit) {
        fetchTVDataFromAPI(
            apiUrl,
            { response -> parseTVData(response) }) { posterUrls, tvShowTitles, _ ->
            val tvShowIds = tvShowTitles.map { it.toIntOrNull() ?: 0 } // Convert titles to IDs
            callback(posterUrls, tvShowIds)
        }
    }

    private fun parseTVData(response: String): Triple<List<String>, List<String>, List<String>> {
        val posterUrls = mutableListOf<String>()
        val tvShowTitles = mutableListOf<String>() // Used as placeholders for IDs
        val firstAirDate = mutableListOf<String>() // Not used in this context

        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            for (i in 0 until resultsArray.length()) {
                val tvShowObject = resultsArray.getJSONObject(i)
                val id = tvShowObject.getInt("id")
                    .toString() // Treat ID as part of titles for callback structure
                val posterPath = tvShowObject.getString("poster_path")
                if (posterPath != "null") {
                    val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
                    posterUrls.add(posterUrl)
                } else {
                    val posterUrl =
                        "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse4.mm.bing.net%2Fth%3Fid%3DOIP.IkbcciGb75wX7U5WeANuDQHaLE%26pid%3DApi&f=1&ipt=a36f8b1fdf094f9245bbbfbf6e0d0908dd49b8c2ae8557f5632a06cce2c36cf2&ipo=images"
                    posterUrls.add(posterUrl)
                }

                tvShowTitles.add(id)
            }
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }

        return Triple(posterUrls, tvShowTitles, firstAirDate)
    }
    fun getTVCastDetails(tvShowId: Int, callback: (List<CastMember>) -> Unit) {
        val url = "https://api.themoviedb.org/3/tv/$tvShowId/credits?api_key=1f443a53a6aabe4de284f9c46a17f64c"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Error fetching TV show details: $e")
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
                            val character = castObject.getString("character")
                            val profilePath = castObject.optString("profile_path", "null")
                            val imageUrl = if (profilePath != "null") "https://image.tmdb.org/t/p/w500$profilePath"
                            else "https://www.nicepng.com/png/full/73-730154_open-default-profile-picture-png.png"
                            castList.add(CastMember(name, character, imageUrl))
                        }
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

    fun getTVStreamingLocationJSON(tmdbId: Int, callback: (Map<String, String>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://streaming-availability.p.rapidapi.com/get?output_language=en&tmdb_id=tv%2F$tmdbId"
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
                    val streamingDetails = parseTVStreamingInfo(responseBody)
                    callback(streamingDetails)
                } else {
                    Log.e("APICallerFORTV", "Request failed with code: ${response.code}")
                    callback(emptyMap())
                }
            } catch (e: IOException) {
                Log.e("APICallerFORTV", "Exception: ${e.message}")
                callback(emptyMap())
            }
        }
    }


    private fun parseTVStreamingInfo(responseBody: String?): Map<String, String> {
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

    fun getTVTrailers(TVId: Int, callback: (List<TrailerMember>) -> Unit) {
        val url = "https://api.themoviedb.org/3/tv/$TVId/videos?api_key=1f443a53a6aabe4de284f9c46a17f64c"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Error fetching cast details: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    val trailerList = mutableListOf<TrailerMember>()
                    try {
                        val jsonObject = JSONObject(responseData)
                        val trailerArray = jsonObject.getJSONArray("results")

                        var key = "null"
                        var YouTubeUrl = "null"


                        for (i in 0 until trailerArray.length()) {
                            val trailerObject = trailerArray.getJSONObject(i)
                            if(trailerObject.getString("type") == ("Trailer") && trailerObject.getString("site") == ("YouTube"))
                            {
                                key = trailerObject.optString("key", "null")
                                if(key != "null")
                                {
                                    YouTubeUrl = "https://www.youtube.com/watch?v=$key"

                                }
                                //If no trailers are found (add code here)?


                                trailerList.add(TrailerMember(key, YouTubeUrl))
                            }
                        }
                        Handler(Looper.getMainLooper()).post {
                            callback(trailerList)
                        }
                    } catch (e: JSONException) {
                        Log.e("JSON Error", "Error parsing JSON: $e")
                    }
                }
            }
        })
    }




}
