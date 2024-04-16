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

class APICaller {

    private val client = OkHttpClient()

    // Generic fetchData function
    fun fetchData(apiUrl: String, processJson: (String) -> Triple<List<String>, List<String>, List<String>>, callback: (List<String>, List<String>, List<String>) -> Unit) {
        val request = Request.Builder().url(apiUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Failed to fetch data: $e")
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

    // Adapted existing getData method to use fetchData
    fun getData(apiUrl: String, textView: TextView, recyclerView: RecyclerView, callback: (List<String>, List<Int>) -> Unit) {
        fetchData(apiUrl, { response -> parseAndDisplayData(response, textView) }) { posterUrls, movieTitles, _ ->
            val movieIds = movieTitles.map { it.toIntOrNull() ?: 0 } // Convert titles to IDs
            callback(posterUrls, movieIds)
        }
    }

    private fun parseAndDisplayData(response: String, textView: TextView): Triple<List<String>, List<String>, List<String>> {
        val posterUrls = mutableListOf<String>()
        val movieTitles = mutableListOf<String>() // Used as placeholders for IDs
        val movieYears = mutableListOf<String>() // Not used in this context

        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            for (i in 0 until resultsArray.length()) {
                val movieObject = resultsArray.getJSONObject(i)
                val id = movieObject.getInt("id").toString() // Treat ID as part of titles for callback structure
                val posterPath = movieObject.getString("poster_path")
                val posterUrl = if (posterPath != "null") "https://image.tmdb.org/t/p/w500$posterPath"
                else "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmedia.istockphoto.com%2Fvectors%2Fno-image-available-icon-vector-id1216251206%3Fk%3D20%26m%3D1216251206%26s%3D170667a%26w%3D0%26h%3DA72dFkHkDdSfmT6iWl6eMN9t_JZmqGeMoAycP-LMAw4%3D&f=1&nofb=1"

                posterUrls.add(posterUrl)
                movieTitles.add(id)
            }
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }

        return Triple(posterUrls, movieTitles, movieYears)
    }

    // Reused the existing specialized methods for other functionality
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

    fun getMovieStreamingLocationJSON(tmdbId: Int, callback: (Map<String, String>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
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
                    callback(emptyMap())
                }
            } catch (e: IOException) {
                Log.e("APICaller", "Exception: ${e.message}")
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
